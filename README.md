# Nomi(鑿、ノミ)

コマンドラインツールをClojureScriptで書くための環境を作ってみた。登場人物は:

- [lumo(https://github.com/anmonteiro/lumo)](https://github.com/anmonteiro/lumo)
- [nexe(https://github.com/nexe/nexe)](https://github.com/nexe/nexe)
- Node.js
- Docker
- make

結論を先に書くと、「ちょっと微妙」。いまのlumoはSocket REPLしかサポートしてないので、Vim(fireplace)とつながらない。これは痛い。

ソースは[ここ(https://github.com/gpsoft/nomi)](https://github.com/gpsoft/nomi)。


# Dockerイメージ

DockerイメージはNode.jsのオフィシャルイメージをベースにし、あらかじめlumoとnexeをglobalにインストールしておく([Dockerfile](https://github.com/gpsoft/nomi/blob/master/docker/Dockerfile))。

イメージのビルドやコンテナの開始はmakeコマンドで。

```shell-session
reventon ~/dev $ git clone https://github.com/gpsoft/nomi.git
reventon ~/dev $ cd nomi
reventon ~/dev/nomi $ make
Usage:
make image
make dev
make attach
```

開発中は、ホスト側のカレントディレクトリが、コンテナ側の`~/proj`として見える。ソースはホスト側で編集し、`npm`, `lumo`などのコマンドはコンテナ側で叩く。

```shell-session
reventon ~/dev/nomi $ make dev
maru@nomi:~/proj$
```

# Hello

まずは、Node.jsでHelloWorldする。

```shell-session
$ mkdir hello
$ cd hello
```

##### index.js

```
#!/usr/bin/env node

console.log('Hello!');
```

`node`コマンドで実行。

```shell-session
$ node index.js
Hello!
```

shebang付きなので、`chmod`しておけば、直接起動可能。

```shell-session
$ chmod 755 index.js
$ ./index.js
Hello!
```

`package.json`を`npm`で自動生成。

```shell-session
$ npm init --yes
```

ちょっと編集する。

##### package.json
```
{
  "name": "hello",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "start": "node index.js",     ←これを追加
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "keywords": [],
  "author": "",
  "license": "ISC"
}
```

`scripts`セクションに書いたコマンドは、`npm run`で実行できる。

```shell-session
$ npm run start

> hello@1.0.0 start /home/maru/proj/hello
> node index.js

Hello!
```

ログが邪魔なら、`--silent`オプション。また、`start`と`test`は特別なので`run`を省略可能。

```shell-session
$ npm start --silent
Hello!
```

# Nodeパッケージを使う

試しに`moment`というパッケージを使ってみる。

```shell-session
$ cd ..
$ mkdir moment
$ cd moment
$ npm init --yes
```

このままだと、`npm`が自動生成するパッケージ名と、使いたいパッケージ名が同じなので、マズイらしい。なので「スコープ」を付ける(名前空間みたいなものか?)。

##### package.json
```
{
  "name": "@gpsoft/moment",    ←これ
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "keywords": [],
  "author": "",
  "license": "ISC",
}
```

`moment`をローカルにインストールする。

```shell-session
$ npm install moment --save
```

インストール先は、`./node_modules`になる。また、`--save`オプションを付けることにより、このパッケージへの依存性が`package.json`へ記録される。

あとは使うだけ。

```
#!/usr/bin/env node

const moment = require('moment');
console.log(moment().format('YYYY-MM-DD hh:mm:ss'));
```

いま気付いたけど、`hh`だと12時間制か。

```shell-session
$ node index.js
2019-03-09 07:29:04
$ date
Sat Mar  9 19:29:10 JST 2019
```


# Hello lumo

Node.jsとnpmの基本が分かったところで、次はLumoでもHelloWorldしとく。

```shell-session
$ cd ..
$ mkdir hello_lumo
$ cd hello_lumo
```

ようやくClojureScriptのコードが登場。

##### main.cljs

```clojure
#!/usr/bin/env lumo

(println "Hello!")
```

`lumo`コマンドで実行。

```shell-session
$ lumo main.cljs
Hello!
```


# LumoでNodeパッケージを使う

```shell-session
$ cd ..
$ mkdir moment_lumo
$ cd moment_lumo
$ npm init --yes
$ npm install moment --save
```

適当にコードを書く。ファイルシステムにアクセスするために、標準パッケージ`fs`(インストール不要)も使ってみる。

##### main.cljs
```clojure
#!/usr/bin/env lumo

(require 'moment
         'fs
         '[clojure.pprint :as pp])

(defn pp-str
  [data]
  (with-out-str
    (pp/pprint data)))

(defn datetime
  []
  (-> (moment)
      (.format "YYYY-MM-DD hh:mm:ss")))

(defn read-json
  [fpath]
  (-> (fs/readFileSync fpath "utf8")
      (js/JSON.parse)
      (js->clj :keywordize-keys true)))

(defn write-str
  [fpath s]
  (fs/writeFileSync fpath s))
```

さて、そろそろREPLしよう。ソースをロードしつつREPLを起動するには、`lumo --init main.cljs --repl`だ。`package.json`に書いておこう。

```
  "scripts": {
    "repl": "lumo --init main.cljs --repl",
    "test": "echo \"Error: no test specified\" && exit 1"
  },
```

`npm run repl`すれば、REPLが起動するので、いろいろ試せる。

```shell-session
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

# ClojureScriptのコンパイル

Lumoはコンパイルもできる(JSに変換するってことね)。

```shell-session
$ cd ..
$ mkdir build
$ cd build
$ npm init --yes
$ npm install moment --save
```

少しリアルに行こう。

- パッケージ名は`bmay`とする
- ソースは、`src/bmay/`に置く
- メイン関数を名前空間`bmay.core`に定義する

[`core.cljs`](https://github.com/gpsoft/nomi/blob/master/build/src/bmay/core.cljs)の重要な部分を抜粋しておく。

##### core.cljs
```clojure
(ns bmay.core
  (:require moment
            fs
            [cljs.pprint :as pp]))

...

(defn -main
  [& args]
  (let [fpath "result.txt"]
    (println "Writing to" fpath)
    (write-str
      fpath
      (str
        (datetime)
        \newline
        (pp-str (read-json "package.json"))))
    (println "Done")))

(set! *main-cli-fn* -main)    ;; ←ここが重要
```

次にビルド用のスクリプトを書く(ビルドするのにスクリプトを書く必要がある、というのはClojureScriptでは普通かな?)。

##### build.cljs
```clojure
(require '[lumo.build.api :as b])

(b/build
  "src"
  {:main 'bmay.core
   :output-to "bmay.js"
   :optimizations :none
   :target :nodejs})
```

オプション仕様は、本家ClojureScriptコンパイラと同じらしい。最適化の`:whitespace`は使えないようだ。`:none`の場合、複数のJSファイルに分けて出力される。`bmay.js`から`require`しといてほしい名前空間を、`:main`オプションに指定する。

少し最適化するなら、こんな感じ:

##### build-simple.cljs
```clojure
(require '[lumo.build.api :as b])

(b/build
  "src"
  {:output-to "bmay.js"
   :optimizations :simple
   :target :nodejs})
```

この場合は、1つのJSファイルにまとまるので、`:main`オプションは不要みたい。

ビルド実行コマンドを`package.json`に書いておこう。

```
  "scripts": {
    "repl": "lumo --init src/bmay/core.cljs --repl",
    "build": "lumo --classpath src build.cljs",
    "build-simple": "lumo --classpath src build-simple.cljs",
    "nexe": "nexe bmay.js -t windows-x64-10.9.0 --output out/bmay.exe",
    "test": "echo \"Error: no test specified\" && exit 1"
  },
```

`--classpath`オプションで、ソースの場所を指示する。`nexe`コマンドは、あとで使うやつ。

ビルドする。

```shell-session
$ npm run build
```

不思議なwarningが出た。

```
WARNING: Use of undeclared Var cljs.pprint/pprint at line 9
```

調べてみたけど良くわからない。高速化のための遅延ローディングが影響しているかもしれない。一応`bmay.js`が出力されたので良しとする。また`build-simple`の方は、最適化のため1分前後かかるので、フリーズと勘違いしないようにしよう。


# Nexe

Nexeは、Node.jsアプリを1つの実行ファイルへパックするツール。

Dockerイメージにインストール済みで、そのバージョンは(いまなら)`v2.0.0-rc.34`になるはずだが、依存関係が壊れている([https://github.com/nexe/nexe/issues/585](https://github.com/nexe/nexe/issues/585))ようで、`fuse-box`とか`TypeScript`関係のエラーが出てしまう。そこで、暫定的に次期バージョン(β版)をインストールする。

```shell-session
$ npm install --global nexe@next
```

コンテナに入ったあとでglobalインストールすると、`~/.npm-global`の下に入るように環境変数を設定してある(PATHも通してある)。

`build-simple`でコンパイルした`bmay.js`を、Windows用のexeへ変換してみよう(最適化`:none`のやつだとパックできない)。

```shell-session
$ npm run nexe
```


# コマンドラインツール開発

最後に、簡単なコマンドラインツールを書いてみた。`cheerio`というパッケージを使って、とあるWebページをスクレイピングする。`cheerio`はNode.js界のjQueryみたいなものらしい。Clojureのスクレイピングなら`enlive`を使いたいところだがClojureScriptには対応してない。

```shell-session
$ cd ..
$ mkdir rtay
$ cd rtay
```

##### core.cljs
```clojure
(ns rtay.core
  (:require request
            cheerio))

(def ^:private url "https://news.yahoo.co.jp/topics")

(defn- for-node
  [$ $nodes f]
  (.each $nodes #(f $ ($ %2))))

(defn- has-photo?
  [$topic-a]
  (pos? (.-length (.find $topic-a "span.icPhoto"))))

(defn- is-new?
  [$topic-a]
  (pos? (.-length (.find $topic-a "span.icNew"))))

(defn- link
  [$topic-a]
  (.attr $topic-a "href"))

(defn- heading!
  [$topic-a]
  (.remove (.find $topic-a "span"))
  (.text $topic-a))

(defn- render-topic
  [h l n? p?]
  (let [n (if n? "[N]" "[ ]")
        p (if p? "[P]" "[ ]")]
    (str n p h "(" l ")")))

(defn- topic
  [$ $topic]
  (let [$a (.find $topic "a")
        new? (is-new? $a)
        photo? (has-photo? $a)
        heading (heading! $a)]
    (println (render-topic heading (link $a) new? photo?))))

(defn- category
  [$ $cat]
  (println (.text $cat))
  (for-node $ (.nextAll $cat "li") topic))

(defn- topics
  [_ _ html]
  (let [$ (.load cheerio html)
        $cats ($ "div.topicsMod li.ttl")]
    (for-node $ $cats category)))

(defn -main
  [& args]
  (request url topics))

(set! *main-cli-fn* -main)
```

実行すると、こんな感じ:

```
$ npm start --silent
国内
[ ][P]天皇即位 秋に26年ぶり恩赦へ(https://news.yahoo.co.jp/pickup/6316450)
[N][P]大阪ダブル選 苦慮する官邸(https://news.yahoo.co.jp/pickup/6316478)
[ ][P]統計不正 安易な前例踏襲(https://news.yahoo.co.jp/pickup/6316473)
[ ][P]法制局長官の発言 広がる波紋(https://news.yahoo.co.jp/pickup/6316445)
[ ][ ]東大 入試の解答を初公表へ(https://news.yahoo.co.jp/pickup/6316426)
[ ][P]マンションで首都直下 避難は(https://news.yahoo.co.jp/pickup/6316419)
[N][P]ベッドに潜む危険 子供は注意(https://news.yahoo.co.jp/pickup/6316518)
[N][P]福岡の116歳女性が世界最高齢(https://news.yahoo.co.jp/pickup/6316504)
[ ][ ]もっと見る(https://news.yahoo.co.jp/list/?c=domestic)
国際
[N][P]北のロケット施設 発射準備か(https://news.yahoo.co.jp/pickup/6316517)
[ ][ ]米朝会談合意なし 北が初報道(https://news.yahoo.co.jp/pickup/6316424)
[N][P]米軍駐留 支払い5割増要求案(https://news.yahoo.co.jp/pickup/6316469)
[ ][ ]米中会談 4月にずれ込みも(https://news.yahoo.co.jp/pickup/6316458)
[ ][P]米 セブン本部と加盟店に亀裂(https://news.yahoo.co.jp/pickup/6316468)
[ ][P]国連人権理がサウジ非難声明(https://news.yahoo.co.jp/pickup/6316406)
[ ][P]華為技術守る 中国が対抗示唆(https://news.yahoo.co.jp/pickup/6316409)
[N][ ]国名変える フィリピンで議論(https://news.yahoo.co.jp/pickup/6316508)
[ ][ ]もっと見る(https://news.yahoo.co.jp/list/?c=world)
経済
[N][ ]レオパレス オーナーに謝罪(https://news.yahoo.co.jp/pickup/6316510)
[ ][P]仮想通貨換金 2億円所得隠し(https://news.yahoo.co.jp/pickup/6316482)
[ ][ ]JTのカナダ法人、会社更生へ(https://news.yahoo.co.jp/pickup/6316498)
[N][P]新刊発売 中国九州さらに遅く(https://news.yahoo.co.jp/pickup/6316514)
...
```

`lumo`には、`--socket-repl`オプションがあって、指定したポートでSocket REPLを開始してくれる。しかし残念ながら、Vim(fireplace)が対応するプロトコルは、現状nREPLオンリー。やはり、エディタがREPLと連動しないというのは厳しい。Emacsな人なら`inf-clojure`というのがあるらしい。
