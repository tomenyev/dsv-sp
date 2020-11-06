# Specification

* <b>Functionality of semester work</b>
  <p>ring chat</p>
* <b>Type of problem</b>
  <p>leader election</p>
* <b>Implemented symmetric algorithm</b>
  <p>CHang-Roberts</p>
* <b>Programming language</b>
  <p>Java</p>
* <b>Message transport</b>
  <p>Java RMI</p>
  
# Instruction

## Requirements
<ol>
  <li>Java 1.8</li>
  <li>Maven</li>
  <li>Git(Optional)</li>
</ol>

## Installing

    git clone https://github.com/tomenyev/dsv-sp.git
    
## Build 

    cd <path to the project folder>
    mvn package
    
## Start

    java -jar target/tomenyev.jar <node address> <existing network address> <log file path>
    
## Args

    <node address> - (REQUIRED) current node address in format ip:port(e.g. 127.0.0.1:8080)
    <existing network address> -(OPTIONAL) existing network address in format ip:port(e.g. 127.0.0.1:8080). Used to join existing network
    <log file path> - (REQUIRED) path to the log file

## Program commands

    s    print node status
    ie   initialize leader election
    m    broadcast text message
    jn   join existing network(eg. 127.0.0.1:8080)
    f    fix network(missing node address)
    h    print commands
    q    safety quit the network
    fq   forcibly quit the network
    l    print complete log from the log file


