#?(:clj  (ns cursor.write-only-equality-test
           (:require [clojure.test :refer [deftest testing is]]
                     [cursor.core :refer [cursor ->WriteOnlyCursor]]))
   :cljs (ns cursor.write-only-equality-test
           (:require-macros [cljs.test :refer [deftest testing is]])
           (:require [cljs.test]
                     [cursor.core :refer [cursor ->WriteOnlyCursor]])))


(deftest same-leaf-same-path []
  (let [initial {:a {:b 1}}
        store (atom initial)
        cur1 (->WriteOnlyCursor (cursor store))
        cur2 (->WriteOnlyCursor (cursor store))]
    (is (= cur1 cur2))
    (is (= (cur1 [:a]) (cur2 [:a])))))


(deftest same-leaf-different-path []
  (let [initial {:a {:b 1} :c {:b 1}}
        store (atom initial)
        cur (->WriteOnlyCursor (cursor store))]
    (is (not= (cur [:a]) (cur [:c])))))


(deftest different-leaf-same-path []
  (let [initial {:a {:b 1}}
        store (atom initial)
        cur-before (->WriteOnlyCursor (cursor store))
        _ (swap! (cur-before [:a :b]) inc)
        cur-after (->WriteOnlyCursor (cursor store))]

    (is (= cur-before cur-after))
    (is (= (cur-before [:a]) (cur-after [:a])))))


(deftest two-stores []
  (let [initial {:a {:b 1}}
        store1 (atom initial)
        store2 (atom initial)
        cur1 (->WriteOnlyCursor (cursor store1))
        cur2 (->WriteOnlyCursor (cursor store2))]
    (is (not= cur1 cur2))
    (is (not= (cur1 [:a]) (cur2 [:a])))))
