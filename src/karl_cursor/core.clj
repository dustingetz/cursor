(ns karl-cursor.core)



(def store (atom {:a {:b 1}, :xs [1 2 3]}))

(deref store)
@store
(.deref store)

(.swap! store (fn [x] (inc x)))

(swap! store #(inc %))

(swap! store identity)

(def store (atom {:a {:b 1}}))

(deref store)
({:a 1} :a)

([1 2 3] 1)
([1 2 3])


(list 1 2 3)
(next (list 1 2 3))

(:b
  (:a
    (deref store)))

(-> (deref store) (:a) (:b))

(macroexpand '(-> (deref store) :a :b))

(get-in @store [:a :b])

(reduce + [10 1 2 3])

(reduce get @store [:a :b])
(reduce get [@store, :a, :b])

(list 1 2 (+ 3 0))
'(1 2 (+ 3 0))
`(1 2 ~(+ 3 0))

(update [1 2 3] 1 (constantly 5))

(update @store :a #(update % :b (constantly 5)))

;(-> @store
;    #(update :a %)
;    #(update :b % (constantly 5)))

;(reduce update [@store :a :b (constantly 5)])

(update-in @store [:xs] #(conj % 9))

(-> @store
    (update-in [:xs] #(conj % 9))
    (update-in [:xs] #(conj % 9)))

(update @store :a #(update % :b (constantly 5)))
;(swap! store #(inc %))
@store
(swap! store #(update-in % [:a :b] (constantly 5)))


(defrecord Cursor [value swap path])
(let [c (Cursor. nil nil nil)]
  (assoc c :value 10))

(defprotocol IFoo
  (foo [this])
  (sum [this c]))

(deftype Foo [a b]
  IFoo
  (foo [this] nil)
  (sum [this c] (+ (get this a) (get this b) c))
  )

(-> (new Foo 1 2)
    (.sum 3))

(defprotocol ICursor
  (refine [this a])
  (value [this])
  (swap [this newvalue])
  (= [this other])
  )

(deftype Cursor [store, path, value]
  ICursor
  (refine [this a] (new Cursor store (conj path a) (get-in value (conj path a))))
  (value [this] value)
  (swap [this f] (swap! store #(update-in % path f)))
  )

(defn buildCursor [store] (new Cursor store [] @store))

(def store (atom {:a {:b 1}, :xs [1 2 3]}))

(def x (buildCursor store))

(.value x)

(-> x (.refine :a) (.value))

(-> x (.refine :a) (.swap (constantly 2)))
