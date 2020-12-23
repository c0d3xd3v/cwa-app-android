package de.rki.coronawarnapp.contactdiary.ui.day.tabs.location

import android.view.ViewGroup
import de.rki.coronawarnapp.R
import de.rki.coronawarnapp.contactdiary.model.ContactDiaryLocation
import de.rki.coronawarnapp.contactdiary.util.SelectableItem
import de.rki.coronawarnapp.databinding.ContactDiaryLocationListItemBinding
import de.rki.coronawarnapp.ui.lists.BaseAdapter
import de.rki.coronawarnapp.util.lists.BindableVH
import de.rki.coronawarnapp.util.lists.diffutil.AsyncDiffUtilAdapter
import de.rki.coronawarnapp.util.lists.diffutil.AsyncDiffer

class ContactDiaryLocationListAdapter(
    private val itemTypeString: String,
    private val onTappedCallback: (item: SelectableItem<ContactDiaryLocation>) -> Unit
) : BaseAdapter<ContactDiaryLocationListAdapter.CachedLocationViewHolder>(),
    AsyncDiffUtilAdapter<SelectableItem<ContactDiaryLocation>> {

    override val asyncDiffer: AsyncDiffer<SelectableItem<ContactDiaryLocation>> = AsyncDiffer(this)

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = data[position].stableId

    override fun onCreateBaseVH(parent: ViewGroup, viewType: Int): CachedLocationViewHolder =
        CachedLocationViewHolder(itemTypeString, parent)

    override fun onBindBaseVH(holder: CachedLocationViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = data[position]
        holder.itemView.setOnClickListener {
            onTappedCallback(item)
        }
        holder.bind(item)
    }

    class CachedLocationViewHolder(
        private val itemTypeString: String,
        parent: ViewGroup
    ) : BaseAdapter.VH(R.layout.contact_diary_location_list_item, parent),
        BindableVH<SelectableItem<ContactDiaryLocation>, ContactDiaryLocationListItemBinding> {
        override val viewBinding = lazy { ContactDiaryLocationListItemBinding.bind(itemView) }

        override val onBindData: ContactDiaryLocationListItemBinding.(
            key: SelectableItem<ContactDiaryLocation>,
            payloads: List<Any>
        ) -> Unit =
            { key, _ ->
                contactDiaryLocationListLineName.text = key.item.locationName
                contactDiaryLocationListLine.contentDescription = "$itemTypeString ${key.item.locationName}"
                when (key.selected) {
                    true -> contactDiaryLocationListLineIcon.setImageResource(R.drawable.ic_selected)
                    false -> contactDiaryLocationListLineIcon.setImageResource(R.drawable.ic_unselected)
                }
            }
    }
}
