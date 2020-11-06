# Specifikace zadání

* <b>funkcionalita semesrální práce</b>
  <p>chat</p>
* <b>typ problému</b>
  <p>volba vůdce</p>
* <b>implementovaný symetrický algoritmus</b>
  <p>CHang-Roberts</p>
* <b>programovací jazyk</b>
  <p>Java</p>
* <b>transport zpráv</b>
  <p>Java RMI</p>
  
# Návod jak práci zprovoznit
## Požadavky na spuštění
<ol>
  <li>Java 1.8</li>
  <li>Maven</li>
  <li>Git(Optional)</li>
</ol>

## Instalace

    git clone https://github.com/tomenyev/dsv-sp.git
    
## Sestavení 

    cd <path to the project folder>
    mvn package
    
## Spuštění

    java -jar target/tomenyev.jar <node address> <existing network address> <log file path>
    
## Args

    <node address> - (REQUIRED) current node address in format ip:port(e.g. 127.0.0.1:8080)
    <existing network address> -(OPTIONAL) existing network address in format ip:port(e.g. 127.0.0.1:8080). Used to join existing network
    <log file path> - (REQUIRED) path to the log file
   

