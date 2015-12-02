(ns karl-cursor.core)


(defprotocol ICursor
  (refine [this path] [this path not-found])
  (value [_])
  (swap [_ f])
  )


(deftype Cursor [value, swap-fn!]
  ICursor
  (refine [this path] (.refine this path nil))
  (refine [_ path not-found]
    (new Cursor
         (get-in value [path] not-found)
         (fn [f]
           (swap-fn! #(update-in % [path] f)))))
  ;swap is being applied too soon (before d is defined)
  (value [_] value)
  (swap [_ f] (swap-fn! f))
  )


(defn buildCursor [store] (new Cursor @store #(swap! store %)))


(comment
  (def store (atom {:a {:b 1}, :xs [1 2 3]}))
  (def cur (buildCursor store))
  (.value cur)
  (-> cur (.refine :a) (.value))
  (-> cur (.refine :a) (.swap (constantly 2)))
  (-> cur (.refine :c {:d 10}) (.refine :d) (.value))
  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap identity))
  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap (constantly 11)))
  @store


  (swap! store #(update-in % [:a :b] inc))
  (swap! store update-in [:a :b] inc)
  )




