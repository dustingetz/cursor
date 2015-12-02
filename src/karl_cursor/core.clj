(ns karl-cursor.core)


(defprotocol ICursor
  (refine [_ a])
  (value [_])
  (swap [_ f])
  )


(deftype Cursor [value, swap-fn!]
  ICursor
  (refine [_ path] (new Cursor
                        (get-in value [path])
                        (fn [f]
                          (swap-fn! #(update-in % [path] f)))))
  (value [_] value)
  (swap [_ f] (swap-fn! f))
  )


(defn buildCursor [store] (new Cursor @store #(swap! store %)))


(comment
  (def store (atom {:a {:b 1}, :xs [1 2 3]}))
  (def x (buildCursor store))
  (.value x)
  (-> x (.refine :a) (.value))
  (-> x (.refine :a) (.swap (constantly 2))))
