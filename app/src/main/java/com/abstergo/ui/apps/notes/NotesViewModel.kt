package com.abstergo.ui.apps.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abstergo.data.AppDatabase
import com.abstergo.data.NoteDao
import com.abstergo.data.NoteEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao: NoteDao = AppDatabase.getInstance(application).noteDao()

    val notes: StateFlow<List<NoteEntity>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedNoteId = MutableStateFlow<Long?>(null)
    val selectedNoteId: StateFlow<Long?> = _selectedNoteId.asStateFlow()

    private val _editingTitle = MutableStateFlow("")
    val editingTitle: StateFlow<String> = _editingTitle.asStateFlow()

    private val _editingBody = MutableStateFlow("")
    val editingBody: StateFlow<String> = _editingBody.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    fun createNewNote() {
        _editingTitle.value = ""
        _editingBody.value = ""
        _selectedNoteId.value = null
        _isEditing.value = true
    }

    fun selectNote(note: NoteEntity) {
        _editingTitle.value = note.title
        _editingBody.value = note.body
        _selectedNoteId.value = note.id
        _isEditing.value = true
    }

    fun updateTitle(title: String) {
        _editingTitle.value = title
    }

    fun updateBody(body: String) {
        _editingBody.value = body
    }

    fun saveNote() {
        val title = _editingTitle.value.ifBlank { "Untitled" }
        val body = _editingBody.value

        viewModelScope.launch {
            val noteId = _selectedNoteId.value
            if (noteId != null && noteId > 0) {
                noteDao.updateNote(
                    NoteEntity(
                        id = noteId,
                        title = title,
                        body = body,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                noteDao.insertNote(
                    NoteEntity(
                        title = title,
                        body = body
                    )
                )
            }
            _isEditing.value = false
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            noteDao.deleteNoteById(noteId)
            if (_selectedNoteId.value == noteId) {
                _isEditing.value = false
            }
        }
    }

    fun cancelEditing() {
        _isEditing.value = false
    }
}
