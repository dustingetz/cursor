(ns karl-cursor.core)


(defprotocol ICursor
  (refine [this segment] [this segment not-found])
  (value [_])
  )

(defn root-at [segments f] #(update-in % segments f))

(comment
  (let [f (root-at [:a :b] inc)]
    (f {:a {:b 0}})))

(deftype Cursor [value, swap-fn!]
  ICursor
  (refine [this segment] (.refine this segment nil))
  (refine [_ segment not-found]
    (new Cursor
         (get-in value [segment] not-found)
         (fn [f]
           (swap-fn! (root-at [segment] (fn [v] (f (or v not-found))))))
         ))
  (value [_] value)

  clojure.lang.IAtom
  (swap [_ f] (swap-fn! f))
  (swap [_ f x] (swap-fn! #(f % x)))
  (swap [_ f x y] (swap-fn! #(f % x y)))
  (swap [_ f x y args] (swap-fn! #(apply f % x y args)))
  )


(defn buildCursor [store] (new Cursor @store #(swap! store %)))


(comment
  (def store (atom {:a {:b 1}, :xs [1 2 3]}))
  (def cur (buildCursor store))
  (-> cur (.swap (fn [x] x)))
  (-> cur (.refine :c {:d 10}) (.refine :d)  (.swap (fn [x] x)))

  (-> cur (.refine :c {:d 10}) (.refine :d) (.value))

  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap (constantly 11)))


  (-> cur (.refine :c {:d 10}) (.swap #(merge % {:z 99})))
  (-> cur (.refine :c {:d 10}) (.swap merge {:z 99}))
  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap #(+ % 1 2 3)))
  (-> cur (.refine :c {:d 10}) (.refine :d) (swap! + 1 2 3))

  @store
  )
