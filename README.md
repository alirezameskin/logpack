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
    format: 'dd/MMM/yyyy:HH:mm:ss Z'
    sources:
      - "time"

  - type: "status-remapper"
    sources:
      - "http.status_category"

  - type: "string-builder-processor"
    template: "Request {{method}} - {{path}}"
    target: "message"

  - type: "message-remapper"
    sources:
      - "message"

  - type: "remapper"
    sources:
      - "remote"
    target: "user.ip"
    preserveSource: false
    overrideOnConflict: false

  - type: "user-agent-parser"
    sources:
      - "agent"
    target: "http.useragent"
```

 
```bash
cat /var/log/nginx/access.log | logpack -c nginx.yaml
```