#NFS-based automatic file sync application

In this project, we are implemented a NFS version 2 client (and dependencies) in Java that will be able to:

1. Watch a folder in a local filesystem for changes, both structural and content
2. Establish a connection to a remote NFS server
3. In case of changes to the local filesystem, propagate the changes to the remote server
4. Restore files/folders from the remote server on demand
5. Encrypt the content of files before transferring to the server, and decrypt the content from the remote server when restoring files
6. Split the content of files using a secret sharing scheme before transferring to multiple servers, and joining the shares from different servers when restoring files


##The synchronization
(a) A file system watcher capable of intercepting events and reading files and folders from a local file system
(b) A MOUNT client able to retrieve the file handle of the share root
(c) An NFS client able to:
    i. Create, read, and navigate folders ii. Read and set attributes
    iii. Create, read and write files
(d) A helper application to restore files from the server on demand, i.e., read files from the server and writing them locally.

##The encryption
(a) Use AES to encrypt and decrypt data
(b) Generate, persist and export encryption keys
(c) Extend the helper app to restore encrypted files

##The advanced security
(a) Extend the synchronization code to support more than one server in parallel
(b) Use Shamir’s secret sharing scheme to split and join data for two separate servers 
(c) Extend the helper app to restore files that have been split between two servers
