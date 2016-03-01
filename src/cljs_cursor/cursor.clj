(ns cljs-cursor.cursor
    (:require [cljs-cursor.root-at :refer [root-at]]))

(defprotocol ICursor
             (refine [this segments] [this segments not-found])
             (swap [_ f])
             )


(deftype Cursor [value, swap-fn!]
         clojure.lang.IDeref
         (deref [_] value)

         ICursor
         (refine [this segments] (refine this segments nil))
         (refine [_ segments not-found]
                 (new Cursor
                      (get-in value segments not-found)
                      (fn [f]
                          (swap-fn! (root-at segments (fn [v] (f (or v not-found))))))
                      ))
         (swap [_ f] (swap-fn! f))
         )

(defn buildCursor [store] (new Cursor @store #(swap! store %)))
