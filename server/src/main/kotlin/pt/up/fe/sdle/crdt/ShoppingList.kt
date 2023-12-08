package pt.up.fe.sdle.crdt

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItem(val name: MVRegister<String>, val count: CCounter) : DotsCRDT<ShoppingListItem> {
    override fun merge(
        other: ShoppingListItem,
        mergeDots: Boolean,
    ): ShoppingListItem {
        name.merge(other.name, false)
        count.merge(other.count, false)

        if (mergeDots) {
            name.set.dots.merge(name.set.dots)
            count.set.dots.merge(count.set.dots)
        }

        return this
    }
}

@Serializable
data class ShoppingList(
    val id: String,
    val name: MVRegister<String>,
    val items: AWMap<String, ShoppingListItem>,
    val dots: DotsContext,
) : DotsCRDT<ShoppingList> {
    init {
        name.set.dots = dots
        items._dots = dots
        items.map.values.forEach {
            it.count.set.dots = dots
            it.name.set.dots = dots
        }
    }

    override fun merge(
        other: ShoppingList,
        mergeDots: Boolean,
    ): ShoppingList {
        this.name.merge(other.name, false)
        this.items.merge(other.items, false)
        this.dots.merge(other.dots)
        return this
    }
}
