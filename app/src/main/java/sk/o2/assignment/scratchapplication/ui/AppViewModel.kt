package sk.o2.assignment.scratchapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel: ViewModel() {
    private val _cardState = MutableLiveData<CardState>().apply {
        value = CardState.UNSCRATCHED
    }
    private val _uid = MutableLiveData<String>().apply {
        value = ""
    }

    val cardState: LiveData<CardState> = _cardState
    val uid: LiveData<String> = _uid

    fun updateCardState(state: CardState) {
        _cardState.value = state
    }
    fun updateUid(uid: String) {
        _uid.value = uid
    }
}

enum class CardState {
    UNSCRATCHED,
    SCRATCHED,
    ACTIVE
}

