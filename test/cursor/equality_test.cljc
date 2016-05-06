#?(:clj  (ns cursor.equality-test
           (:require [clojure.test :refer [deftest testing is]]
                     [cursor.core :refer [cursor]]))
   :cljs (ns cursor.equality-test
           (:require-macros [cljs.test :refer [deftest testing is]])
           (:require [cljs.test]
                     [cursor.core :refer [cursor]])))


(deftest same-leaf-same-path []
  (let [initial {:a {:b 1}}
        store (atom initial)
        cur1 (cursor store)
        cur2 (cursor store)]
    (is (= cur1 cur2))
    (is (= (cur1 [:a]) (cur2 [:a])))))


(deftest same-leaf-different-path []
  (let [initial {:a {:b 1} :c {:b 1}}
        store (atom initial)
        cur (cursor store)]
    (is (not= (cur [:a]) (cur [:c])))))


(deftest different-leaf-same-path []
  (let [initial {:a {:b 1}}
        store (atom initial)
        cur-before (cursor store)
        _ (swap! (cur-before [:a :b]) inc)
        cur-after (cursor store)]

    (is (not= cur-before cur-after))
    (is (not= (cur-before [:a]) (cur-after [:a])))))


(deftest two-stores []
  (let [initial {:a {:b 1}}
        store1 (atom initial)
        store2 (atom initial)
        cur1 (cursor store1)
        cur2 (cursor store2)]
    (is (not= cur1 cur2))
    (is (not= (cur1 [:a]) (cur2 [:a])))))
