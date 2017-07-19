(ns etl.core
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [org.httpkit.client :as http])
  (:import java.util.zip.ZipInputStream
           java.util.concurrent.Semaphore)
  (:gen-class))

(defn select-date
  [log-line]
  (re-find #"(?m)^([^\|]*)" log-line))

(defn tranform
  [log-line]
  {:date (some-> log-line
             str
             select-date
             second
             identity
             (str/replace #"\s+" "T"))
   :text log-line})


(defn add-bulk
  [list]
  (let [send-data (str/join ""
                            (map #(str "{ \"index\": {}}\n"
                                       "{\"date\" : \"" (:date %)
                                       "\", \"text\" : \"" (:text %) "\"}\n") list))]
    send-data))

(def http-count (atom 0))
(def log-per-req 1e4)
(defn stream-log
  "bulk log from zip file to elasticsearch server"
  [file-name url]
  (let [sem (Semaphore. 5)
        zz  (java.util.zip.ZipFile. file-name)
        zs  (ZipInputStream. (io/input-stream file-name))
        ze  (.getNextEntry zs)]
    (->> ze
         (.getInputStream zz)
         io/reader
         line-seq
         (partition-all log-per-req)
         (map-indexed
          (fn [index data]
               (.acquire sem)
                (http/post url
                   {:body (add-bulk (map tranform data))
                   :headers {"Content-Type" "application/json"}}
                     (fn [_]
                        (.release sem)
                        (swap! http-count inc)
                        (when (= 0 (mod @http-count 10))
                        (println "sended: " (* log-per-req @http-count) "lines of log"))))))
         dorun)))

(defn read-n-stream-log
   "read file in folder limit file count and send to elasticsearch server"
  [log-folder url limit-file]
    (->> log-folder
       io/file
       file-seq
       (drop 1)
       (take limit-file)
       (map #(stream-log (.getPath %) url))
       dorun))
(defn -main
  ([]
    (println "need arguement folder name, elastic search url and limit file"))
  ([log-folder url limit]
    (read-n-stream-log log-folder url (Integer/parseInt limit))))

;(def log-folder "/Users/Home/Project/visual-log/A2")
;(def url "http://localhost:32773/visual-log/monitor/_bulk")
;(-main log-folder url 1 )
