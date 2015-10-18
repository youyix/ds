#NFS-based automatic file sync application

In this project, we are implemented a NFS version 2 client (and dependencies) in Java that will be able to:

1. Watch a folder in a local filesystem for changes, both structural and content
2. Establish a connection to a remote NFS server
3. In case of changes to the local filesystem, propagate the changes to the remote server
4. Restore files/folders from the remote server on demand
5. Encrypt the content of files before transferring to the server, and decrypt the content from the remote server when restoring files
6. Split the content of files using a secret sharing scheme before transferring to multiple servers, and joining the shares from different servers when restoring files


##The synchronization
- A file system watcher capable of intercepting events and reading files and folders from a local file system
- A MOUNT client able to retrieve the file handle of the share root
- An NFS client able to:
    i. Create, read, and navigate folders ii. Read and set attributes
    iii. Create, read and write files
- A helper application to restore files from the server on demand, i.e., read files from the server and writing them locally.

##The encryption
- Use AES to encrypt and decrypt data
- Generate, persist and export encryption keys
- Extend the helper app to restore encrypted files

##The advanced security
- Extend the synchronization code to support more than one server in parallel
- Use Shamir’s secret sharing scheme to split and join data for two separate servers 
- Extend the helper app to restore files that have been split between two servers

#Usage
To compile:
-----------
javac -cp lib/oncrpc.jar src/client/*/*.java  (at ./)

or 

in Eclipse IDE

To run:
-------
at ./bin/
java -cp .:../lib/oncrpc.jar client/kkk/InteractiveControl 192.168.0.12 /Users/cici/nfss /Users/niezhenfei/kkk

or

in Eclipse IDE


Example usage:
-------------

Generate and export key:
java -cp .:../lib/oncrpc.jar clients.Main -export apassword

Run at mode 1
java -cp .:../lib/oncrpc.jar clients.Main -import secretKey.nfskey  192.168.0.12 /Users/cici/nfss ~/kkk

Run at mode 2
java -cp .:../lib/oncrpc.jar clients.Main -mode2 192.168.0.12 /Users/cici/nfss 192.168.0.15 /Users/niezhenfei/nfss ~/kkk