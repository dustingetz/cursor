(ns karl-cursor.core)

(defn root-at [segments f] #(update-in % segments f))

(comment
  (let [f (root-at [:a :b] inc)]
    (f {:a {:b 0}})))

(deftype Cursor [value, swap-fn!]
  clojure.lang.IDeref
  (deref [_] value)

  clojure.lang.IFn
  (invoke [this segments] (.invoke this segments nil))
  (invoke [_ segments not-found]
    (new Cursor
         (get-in value segments not-found)
         (fn [f]
           (swap-fn! (root-at segments (fn [v] (f (or v not-found))))))
         ))

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

  (-> cur (.invoke [:c] {:d 10}) (.invoke [:d])  (.swap (fn [x] x)))
  (-> cur (.invoke [:c] {:d 10}) (.invoke [:d]) (.deref))
  (-> cur (.invoke [:c] {:d 10}) (.invoke [:d]) (.swap (constantly 11)))


  (-> cur (.invoke [:c] {:d 10}) (.swap #(merge % {:z 99})))
  (-> cur (.invoke [:c] {:d 10}) (.swap merge {:z 99}))
  (-> cur (.invoke [:c] {:d 10}) (.invoke [:d]) (.swap #(+ % 1 2 3)))
  (-> cur (.invoke [:c] {:d 10}) (.invoke [:d]) (swap! + 1 2 3))

  @(cur [:a :b :c] {:d 10})
  @(cur)
  @cur

  @store
  )
