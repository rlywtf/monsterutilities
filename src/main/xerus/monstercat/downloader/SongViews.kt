package xerus.monstercat.downloader

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import xerus.ktutil.javafx.controlsfx.FilterableCheckTreeView
import xerus.ktutil.javafx.onFx
import xerus.ktutil.javafx.ui.FilterableTreeItem
import xerus.monstercat.api.Player
import xerus.monstercat.api.Releases
import xerus.monstercat.api.Tracks
import xerus.monstercat.api.response.MusicItem
import xerus.monstercat.api.response.Release
import xerus.monstercat.api.response.Track

abstract class SongView<T : MusicItem>(root: T) : FilterableCheckTreeView<T>(root) {
	var ready = false
	
	init {
		setOnMouseClicked {
			if (it.clickCount == 2) {
				val selected = selectionModel.selectedItem ?: return@setOnMouseClicked
				if (selected.isLeaf) {
					val value = selected.value
					if (value is Release)
						Player.play(value)
					else if (value is Track)
						Player.playTrack(value)
				}
			}
		}
		load()
	}
	
	fun load() {
		GlobalScope.launch {
			fetchItems()
			onFx {
				onReady()
				ready = true
			}
		}
	}
	
	abstract suspend fun fetchItems()
	abstract fun onReady()
}

class TrackView : SongView<Track>(Track(title = "Loading Tracks...")) {
	override suspend fun fetchItems() {
		Tracks.tracks?.sortedBy { it.toString() }?.forEach {
			root.internalChildren.add(FilterableTreeItem(it))
		}
	}
	
	override fun onReady() {
		root.value = Track(title = "Tracks")
	}
}

class ReleaseView : SongView<Release>(Release(title = "Releases")) {
	val roots = HashMap<String, FilterableTreeItem<Release>>()
	override suspend fun fetchItems() {
		Releases.getReleases().forEach {
			roots.getOrPut(it.type) {
				FilterableTreeItem(Release(title = it.type))
			}.internalChildren.add(FilterableTreeItem(it))
		}
	}
	
	override fun onReady() {
		root.internalChildren.addAll(roots.values)
	}
}