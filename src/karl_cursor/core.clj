(ns karl-cursor.core)


(defprotocol ICursor
  (refine [this path] [this path not-found])
  (value [_])
  (swap [_ f] [_ f x] [_ f x y] [_ f x y args])
  )


(deftype Cursor [value, swap-fn!]
  ICursor
  (refine [this path] (.refine this path nil))
  (refine [_ path not-found]
    (new Cursor
         (get-in value [path] not-found)
         (fn [f & args]
           (apply swap-fn!
                  (fn [v & args]
                    (apply update-in
                      (or v not-found)
                      [path] f args
                      ))
                  args))
         ))
  ;swap is being applied too soon (before d is defined)
  (value [_] value)
  (swap [_ f] (swap-fn! f))
  (swap [_ f x] (swap-fn! f x))
  (swap [_ f x y] (swap-fn! f x y))
  (swap [_ f x y args] (apply swap-fn! f x y args))
  )


(defn buildCursor [store] (new Cursor @store #(apply swap! store % %&)))


(comment
  (do
    (def store (atom {:a {:b 1}, :xs [1 2 3]}))
    (def cur (buildCursor store))
    #_ (.value cur)
    #_ (-> cur (.refine :a) (.value))
    #_ (-> cur (.refine :a) (.swap (constantly 2)))
    (-> cur (.refine :c {:d 10}) (.refine :d) (.swap (fn [x] x))))

  (-> cur (.refine :c {:d 10}) (.refine :d) (.value))

  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap (constantly 11)))


  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap #(merge % {:z 99})))
  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap merge {:z 99}))
  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap #(+ 1 2 3)))
  (-> cur (.refine :c {:d 10}) (.refine :d) (.swap + 1 2 3))
  @store


  (swap! store #(update-in % [:a :b] inc))
  (swap! store update-in [:a :b] inc)
  )




