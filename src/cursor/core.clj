(ns cursor.core
  (:require [cursor.root-at :refer [root-at]]
            [cursor.get-in :refer [get-in']]))


(deftype Cursor [value, swap-fn!]
  clojure.lang.IDeref
  (deref [_] value)

  clojure.lang.IFn
  (invoke [this segments] (this segments nil))
  (invoke [this segments not-found] (this segments not-found get))
  (invoke [this segments not-found get']
    (new Cursor
         (get-in' value segments not-found get')
         (fn [f]
           (swap-fn! (root-at segments (fn [v] (f (or v not-found))))))))

  clojure.lang.IAtom
  (swap [_ f] (swap-fn! f))
  (swap [_ f x] (swap-fn! #(f % x)))
  (swap [_ f x y] (swap-fn! #(f % x y)))
  (swap [_ f x y args] (swap-fn! #(apply f % x y args)))
  (reset [o v] (swap! o (constantly v))))


(defn cursor [store] (new Cursor @store #(swap! store %)))