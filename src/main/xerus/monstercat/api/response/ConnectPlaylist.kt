package xerus.monstercat.api.response

import com.google.api.client.util.Key

data class ConnectPlaylist(
		@Key("_id") var id: String = "",
		@Key var name: String = "",
		@Key var public: Boolean = false,
		
		@Key var tracks: List<Track> = ArrayList()
) {
	fun init(): ConnectPlaylist {
		name = name.trim()
		return this
	}
}