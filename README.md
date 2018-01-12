# textmining

---
## Config info:
On every Port there is a Metamap instance running. It is designed for a powerful server which will run
multiple docker containers each with a Metamap server running. The first port is the first port on which metamap
can be connected to. Every subsequent port including the end Port must be running a Metamap instance.
If ports_start_1 = ports_end_1 you have only one Metamap instance running on this host with this one port.
ports_start must be the lower number. The Config currently holds 3 hosts.
metamap_host_1 --- MMHost of a metamap server
metamap_ports_start_1 --- first open Port for metamap connection
metamap_ports_end_1 --- last open port for a metamap connection

---

### Pipeline:
1. getting the article
2. filter docs
3. zoning
4. Deleting stopwords
5. metamap
6. semrep
7. convertation
8. fwd to middleware

