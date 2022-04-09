# ProtoCB Client Agent

## About
ProtoCB Test Bed includes a pool of client agents interacting with a <a href="https://github.com/ProtoCB/server-agent">server agent</a>. The interaction is in accordance with a recipe that the <a href="https://github.com/ProtoCB/controller">ProtoCB controller</a> feeds into the agents while scheduling experiments.

Each client agent is implemented as a spring-boot web application - the code for which can be found in this reposiory. A client agent may be launched by running the following command:

```sh
nohup java -jar <path_to_jar> --agent.host=<agent_ip_address> --controller.url=<controller_url> --storage.bucket=<firebase_storage_bucket> --agent.secret=<secret> --server.port=<agent_port> --protocb.home=<protocb_directory> > <path_to_log_file> &
```

## Design
![Client Agent Design](design.jpg "Client Agent Design")
