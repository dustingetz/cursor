(ns karl-cursor.core)


(defprotocol ICursor
  (refine [this a])
  (value [this])
  (swap [this newvalue])
  )


(deftype Cursor [value, path, swap-fn!]
  ICursor
  (refine [this a] (new Cursor (get-in value (conj path a)) (conj path a) swap-fn!))
  (value [this] value)
  (swap [this f] (swap-fn! #(update-in % path f)))
  )


(defn buildCursor [store] (new Cursor @store [] #(swap! store %)))


(comment
  (def store (atom {:a {:b 1}, :xs [1 2 3]}))
  (def x (buildCursor store))
  (.value x)
  (-> x (.refine :a) (.value))
  (-> x (.refine :a) (.swap (constantly 2))))
