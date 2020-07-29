# LogPack - Log parser

## Sample configuration

```yaml
#nginx.yaml
steps:
  - type: "grok-parser"
    matchRules:
      - '^(?<remote>[^ ]*) (?<host>[^ ]*) (?<user>[^ ]*) \[(?<time>[^\]]*)\] "(?<method>\S+)(?: +(?<path>[^\"]*?)(?<version> +\S*)?)?" (?<status>[^ ]*) (?<size>[^ ]*)(?: "(?<referer>[^\  "]*)" "(?<agent>[^\"]*)")'
      - '^(?<time>\d{4}/\d{2}/\d{2} \d{2}:\d{2}:\d{2}) \[(?<level>\w+)\] (?<pid>\d+).(?<tid>\d+): (?<message>.*)'

  - type: "category-processor"
    categories:
      - name: "OK"
        query: "status:[200 TO 299]"
      - name: "Notice"
        query: "status:[300 TO 399]"
      - name: "Warning"
        query: "status:[400 TO 499]"
      - name: "Error"
        query: "status:[500 TO 599]"
    target: "http.status_category"

  - type: "date-remapper"
    preserveSource: false
    format: 'dd/MMM/yyyy:HH:mm:ss Z'
    sources:
      - "time"

  - type: "status-remapper"
    sources:
      - "http.status_category"

  - type: "string-builder-processor"
    template: "{{method}} {{path}}"
    target: "message"

  - type: "message-remapper"
    preserveSource: false
    sources:
      - "message"

  - type: "remapper"
    sources:
      - "remote"
    target: "http.user_ip"
    preserveSource: false

  - type: "url-parser"
    sources:
      - "path"
    target: "url.details"

  - type: "user-agent-parser"
    sources:
      - "agent"
    target: "http.useragent"

  - type: "arithmetic-processor"
    expression: "Expression"
    target: "target-attribute"
```

 
```bash
echo '176.9.9.94 - - [18/Jul/2020:06:25:03 +0000] "GET /contact/form?lang=en HTTP/1.1" 200 12221 "-" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:78.0) Gecko/20100101 Firefox/78.0"' > /tmp/nginx.log

cat /tmp/nginx.log | java -jar target/scala-2.13/logpack-assembly-0.1.0-SNAPSHOT.jar -p examples/nginx.yaml
```

The output would be a json
```json
{
  "message" : "GET /contact/form?lang=en",
  "level" : "Info",
  "time" : 1595046303000,
  "attributes" : {
    "http" : {
      "useragent" : {
        "deviceClass" : "Computer",
        "os" : "Mac OS X",
        "browserFamily" : "Firefox",
        "version" : "78.0"
      },
      "user_ip" : "176.9.9.94",
      "status_category" : "OK"
    },
    "url" : {
      "details" : {
        "scheme" : null,
        "host" : null,
        "port" : null,
        "path" : "/contact/form",
        "query" : "lang=en",
        "fragment" : null
      }
    },
    "method" : "GET",
    "host" : "-",
    "agent" : "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:78.0) Gecko/20100101 Firefox/78.0",
    "version" : " HTTP/1.1",
    "status" : "200",
    "user" : "-",
    "path" : "/contact/form?lang=en",
    "size" : "12221",
    "referer" : "-"
  }
} 
```
