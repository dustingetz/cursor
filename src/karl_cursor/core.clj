(ns karl-cursor.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(def x (atom nil))

(swap! x (constantly 1))

(def x {:a {:b 42}})

(get x :a)
(get-in x [:a :b])



(defn get-in' [m ks]
  (reduce #(get %1 %2)
          m
          ks))

(defn get-in'' [m ks]
  (reduce get m ks))


(update-in x [:a :b] #(+ %1 1 2 3))

(update-in x [:a :b] + 1 2 3)

(def y (atom {:a {:b 42}}))

(swap! y update-in :d + 1)

(swap! y #(update-in %1 [:c :d] inc))

(swap! y update-in [:c :d] inc)

[:c d]

(defn get-in-atom [path] )



(defrecord Cursor [value swap path])

(defn build-cursor [value swap]
  (Cursor. value swap []))


(defn refine-cursor [cur more-paths]
  (Cursor. (:value cur)
           (:swap cur)
           (concat (:path cur) more-paths)))

(defn cursor-value [cur]
  (get-in (:value cur) (:path cur)))

(comment
  (def store (atom {:a {:b {:c 42}}}))
  (def cur (build-cursor @store #(swap! store %1)))
  (def cur' (refine-cursor cur [:a :b :c]))
  (cursor-value cur')

  ((:swap cur') inc)


  )


(defprotocol ICursor
  (value [this])
  (refine [this more-paths])
  (swap!' [this update-fn])
  )




(deftype Cursor2 [store paths value]

  ICursor
  (value [this] (get-in value paths))
  (refine [this more-paths] (Cursor2. store (concat paths more-paths) value))
  (swap!' [this update-fn] (swap! store update-in paths update-fn))

  )

(defn cursor [store] (Cursor2. store [] @store))


(comment

  (def store (atom {:a { :b 42 }}))
  (def c (cursor store))
  (.value (.refine c [:a :b]))

  (-> c (.refine [:a :b]) (.swap!' inc))

  (.value (.refine c [:a :b]))

  )











