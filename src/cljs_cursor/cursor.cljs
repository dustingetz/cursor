(ns cljs-cursor.cursor
  (:require [cljs-cursor.root-at :refer [root-at]]))

(deftype Cursor [value, swap-fn!]
         IDeref
         (-deref [_] value)

         IFn
         (-invoke [this segments] (this segments nil))
         (-invoke [_ segments not-found]
                 (new Cursor
                      (get-in value segments not-found)
                      (fn [f]
                          (swap-fn! (root-at segments (fn [v] (f (or v not-found))))))
                      ))

         ISwap
         (-swap! [_ f] (swap-fn! f))
         (-swap! [_ f x] (swap-fn! #(f % x)))
         (-swap! [_ f x y] (swap-fn! #(f % x y)))
         (-swap! [_ f x y args] (swap-fn! #(apply f % x y args)))

         IReset
         (-reset! [o v] (swap! o (constantly v)))
         )

(defn buildCursor [store] (new Cursor @store #(swap! store %)))
