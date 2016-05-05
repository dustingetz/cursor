#?(:clj  (ns cursor.get-in-test
           (:require [clojure.test :refer [deftest testing is]]
                     [cursor.get-in :refer [get-in']]))
   :cljs (ns cursor.get-in-test
           (:require-macros [cljs.test :refer [deftest testing is]])
           (:require [cljs.test]
                     [cursor.get-in :refer [get-in']])))

(defn loaded? [m]
  false)


(defn get' [map key not-found]
  (let [thing (get map key)]
    (if (loaded? thing)
      thing
      not-found)))


(get-in' {:a {:href "a"}} [:a] {:href "a" :data "here"} get')