# blogjr

A simple, lightweight blogging application, written in Clojure.

## Usage

Download the source code, then:

1. From the top-level directory, type: lein deps
2. Create a postgresql database called "blogjr", and a role "blogjr" that has full permissions on that database.

From the top level directory, start a REPL with: "lein repl", then:

3. (use 'blogjr.db)
4. (create-posts)
5. (create-users)
6. (create-comments)

Finally, exit the REPL and type:

7. lein ring server

To start up the blog!

## License

Distributed under the Eclipse Public License, the same as Clojure.
