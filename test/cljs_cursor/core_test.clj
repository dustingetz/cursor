(ns cljs-cursor.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [cljs-cursor.core :as cursor]))


(deftest test-root-at []
         (is (= (let [f (cursor/root-at [:a :b] inc)]
                  (f {:a {:b 0}}))
                {:a {:b 1}})))


(def store (atom {:a {:b 1}, :xs [1 2 3]}))


(def cur (cursor/buildCursor store))


(deftest value []
         (is (= (.value cur) {:a {:b 1}, :xs [1 2 3]})))

