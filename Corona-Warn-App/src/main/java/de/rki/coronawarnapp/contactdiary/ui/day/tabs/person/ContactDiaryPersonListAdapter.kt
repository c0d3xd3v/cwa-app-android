package de.rki.coronawarnapp.contactdiary.ui.day.tabs.person

import android.view.ViewGroup
import de.rki.coronawarnapp.R
import de.rki.coronawarnapp.contactdiary.model.ContactDiaryPerson
import de.rki.coronawarnapp.contactdiary.util.SelectableItem
import de.rki.coronawarnapp.databinding.ContactDiaryPersonListItemBinding
import de.rki.coronawarnapp.ui.lists.BaseAdapter
import de.rki.coronawarnapp.util.lists.BindableVH
import de.rki.coronawarnapp.util.lists.diffutil.AsyncDiffUtilAdapter
import de.rki.coronawarnapp.util.lists.diffutil.AsyncDiffer

class ContactDiaryPersonListAdapter(
    private val itemTypeString: String,
    private val onTappedCallback: (item: SelectableItem<ContactDiaryPerson>) -> Unit
) : BaseAdapter<ContactDiaryPersonListAdapter.CachedPersonViewHolder>(),
    AsyncDiffUtilAdapter<SelectableItem<ContactDiaryPerson>> {

    override val asyncDiffer: AsyncDiffer<SelectableItem<ContactDiaryPerson>> = AsyncDiffer(this)

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = data[position].stableId

    override fun onCreateBaseVH(parent: ViewGroup, viewType: Int): CachedPersonViewHolder =
        CachedPersonViewHolder(itemTypeString, parent)

    override fun onBindBaseVH(holder: CachedPersonViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = data[position]
        holder.itemView.setOnClickListener {
            onTappedCallback(item)
        }
        holder.bind(item, payloads)
    }

    class CachedPersonViewHolder(
        private val itemTypeString: String,
        parent: ViewGroup
    ) : BaseAdapter.VH(R.layout.contact_diary_person_list_item, parent),
        BindableVH<SelectableItem<ContactDiaryPerson>, ContactDiaryPersonListItemBinding> {
        override val viewBinding = lazy { ContactDiaryPersonListItemBinding.bind(itemView) }

        override val onBindData: ContactDiaryPersonListItemBinding.(
            key: SelectableItem<ContactDiaryPerson>,
            payloads: List<Any>
        ) -> Unit = { key, _ ->
            contactDiaryPersonListLineName.text = key.item.fullName
            contactDiaryPersonListLine.contentDescription = "$itemTypeString ${key.item.fullName}"
            when (key.selected) {
                true -> contactDiaryPersonListLineIcon.setImageResource(R.drawable.ic_selected)
                false -> contactDiaryPersonListLineIcon.setImageResource(R.drawable.ic_unselected)
            }
        }
    }
}
