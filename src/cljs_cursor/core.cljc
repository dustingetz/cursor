(ns cljs-cursor.core)


(defn root-at [segments f] #(update-in % segments f))


(defprotocol ICursor
  (refine [this segments] [this segments not-found])
  (value [_])
  (swap [_ f])
)


(deftype Cursor [value, swap-fn!]
  ICursor
  (value [_] value)
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
