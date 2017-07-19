### simple etl

simple etl parse log of zip file in folder and send to elasticsearch server by bulk

```bash
lein compile
lein uberjar
```

use by add folder location elasticsearch url and limit file example

```bash
java -jar ./target/etl-0.1.0-SNAPSHOT-standalone.jar /path/to/file http://localhost:9200/log/log/_bulk 1

```

# todo
elasticsearch side
- [ ] assign more ram and cpu to docker node
- [ ] config low disk watermark

app side

 - [x] parse log bug when date not match
 - [ ] add unit test
 - [ ] add main function
 - [ ] make build pipe line
 - [ ] make multiple ES index by using name convention 
         api-{type}-YYYY-MM-DD (example  api-monitor-2017-06-01)
 - [ ] refactor code by using thrad macro for represent data pipe line order
 - [ ] refactor code by seperate side effect from pure
 - [ ] introduce core.async
 - [ ] add integration test
 - [ ] add docker compose
 - [ ] add e2e test
 
  
