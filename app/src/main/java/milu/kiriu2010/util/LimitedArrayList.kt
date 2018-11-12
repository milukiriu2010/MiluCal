package milu.kiriu2010.util

import java.lang.Exception

class LimitedArrayList<E>(initialCapacity: Int)
    : ArrayList<E>(initialCapacity) {

    // リストに格納可能な数
    var limit: Int = 10
        set(value) {
            // 配列のサイズは正のみ許可
            if (limit < 0) {
                throw Exception("limit should be less than size.")
            }

            // もし、現在の配列のサイズより小さい値を指定されたら
            // 超えた分を削除する
            if (value < size) {
                removeRange(value,size)
            }
            field = value
        }

    constructor(initialCapacity: Int, limit: Int = 10): this(initialCapacity) {
        // 配列のサイズは正のみ許可
        if (limit < 0) {
            throw Exception("limit should be less than size.")
        }
        this.limit = limit
    }

    override fun add(index: Int, element: E) {
        val ret = super.add(index, element)

        // リミットを超えていたら最後の要素を削除
        while ( size > limit ) {
            super.remove(super.get(size-1))
        }

        return ret
    }

    override fun add(element: E): Boolean {
        val ret = super.add(element)

        // リミットを超えていたら最後の要素を削除
        while ( size > limit ) {
            super.remove(super.get(size-1))
        }

        return ret
    }

}