cursor
===============

> Portable .cljc cursors backed by an atom

## What is a cursor

Cursors are a read/write view into a subtree of an atom. I first saw this abstraction in Om. This implementation is decoupled from any rendering library and is very small.

Cursors are useful in UI programming, because UIs are tree shaped and naturally have tree-shaped state. 
Cursors let your app hold all its state in one place at the root of the UI tree; thus the root is stateful, and all downtree views are stateless.
 
## Example
 
```clojure
(ns example (:require [cursor.core :refer cursor]))

(defn Counter [label cur]
  [:div
   label (pr-str @cur)
   [:button {:on-click #(swap! cur inc)} "+1"]])

(defn Home [cur]
  [:div
   [Counter "Counter A" (cur [:a])]
   [Counter "Counter B" (cur [:b])]])

(def app-state (atom {:foo {:a 0 :b 0}}))

(defn render! []
  (let [cur (cursor app-state)]
    (reagent/render [Home cur] (.getElementById js/document "root"))))
```

## API

Cursors look and act like an atom, they can be refined to produce a more nested cursor which is a read/write view into a 
subtree of the atom. Cursors have value semantics so they are suitable for use in the React.js component lifecycle. One implication of value semantics is that once refined, a cursor can't escape it's subtree, because it's value is that subtree.

 * IFn, e.g. `(cur [:a])` Refine an existing cursor `cur` with a path vector `[:a]` to produce a new cursor
 * IDeref e.g. `@cur` Inspect the value in the cursor (deref here has value semantics, unlike atoms which return latest)
 * `hash`, `=`, `swap!`, `reset!`, etc all work as expected. Effects impact the backing atom, not the cursor itself.
 * `cursor.core/cursor` e.g. `(cursor my-atom)` Construct a cursor backed by an atom.
 * `cursor.core/virtual-cursor` Low-level cursor constructor interface which doesn't assume atom, see source 

## Project Maturity

master is stable, there is a test suite.

## License

_`cursor` is governed under the MIT License._
