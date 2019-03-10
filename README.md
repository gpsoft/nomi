# Nomi(鑿、ノミ)

ブログ: [LumoとNode.jsで作るCLJS環境](http://gpsoft.dip.jp/gpblog/posts-output/2019-03-09-lumo/)

How to write a cli tool in ClojureScript? You only need docker, make command,  and this repo.

# Docker image

```shell-session
host ~/dev $ git clone https://github.com/gpsoft/nomi.git
host ~/dev $ cd nomi
host ~/dev/nomi $ make image
```

The docker image, which is based on an official Node.js image, has necessary tools installed:

- node
- npm
- lumo
- nexe

Just start the container to use them like so.

```shell-session
host ~/dev/nomi $ make dev
you@nomi:~/proj$
```

# Samples

- `hello`
- `moment`
- `hello_lumo`
- `moment_lumo`
- `build`
- `rtay`

## Hello

`hello` directory has a minimal Node.js script.

```shell-session
$ node index.js
Hello!
```

## Using Node packages

Navigate to `moment` directory. It uses `moment` package.

```
#!/usr/bin/env node

const moment = require('moment');
console.log(moment().format('YYYY-MM-DD hh:mm:ss'));
```

I should have used `HH` instead of `hh`?

```shell-session
$ npm install
$ node index.js
2019-03-09 07:29:04
$ date
Sat Mar  9 19:29:10 JST 2019
```

## Hello lumo

Lumo version of HelloWorld is in `hello_lumo` directory.

```clojure
#!/usr/bin/env lumo

(println "Hello!")
```

Easy to run with lumo.

```shell-session
$ lumo main.cljs
Hello!
```

## Using Node packages(Lumo version)

In the `moment_lumo` directory, it uses `moment` and standard package `fs` with lumo.

You can start a REPL session like so.

```shell-session
$ npm install
$ npm run repl --silent
Lumo 1.9.0
ClojureScript 1.10.439
Node.js v10.9.0
 Docs: (doc function-name-here)
       (find-doc "part-of-name-here")
 Source: (source function-name-here)
 Exit: Control+D or :cljs/quit or exit

cljs.user=> (write-str
       #_=>   "result.txt"
       #_=>   (str
       #_=>     (datetime)
       #_=>     \newline
       #_=>     (pp-str (read-json "package.json"))))
nil
cljs.user=>
```

## Compile cljs into js

Lumo can compile cljs into js. `build` directory shows how.

Make sure you have:

- a namespace
- the `-main` function
- `*main-cli-fn*` set to the `-main`

And you need build scripts. One for non-optimized(builds faster):

```clojure
(require '[lumo.build.api :as b])

(b/build
  "src"
  {:main 'bmay.core
   :output-to "bmay.js"
   :optimizations :none
   :target :nodejs})
```

One for optimized(slow but can be packed with `nexe`):

### optimized

```clojure
(require '[lumo.build.api :as b])

(b/build
  "src"
  {:output-to "bmay.js"
   :optimizations :simple
   :target :nodejs})
```

Here is how to run the script.

```shell-session
$ npm install
$ npm run build
$ npm run build-simple
```

Like uberjar in Clojure, you can make all-in-one executable file with Nexe.

```shell-session
$ nexe bmay.js -t windows-x64-10.9.0 --output out/bmay.exe
```

Caveat: `nexe v2.0.0-rc.34` [has a problem](https://github.com/nexe/nexe/issues/585); you may have to install next version:

```shell-session
$ npm install --global nexe@next
```

## A real tool

`rtay` directory has a (kind of) real cli tool which uses cheerio and request package.

