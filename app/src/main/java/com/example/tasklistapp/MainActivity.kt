package com.example.tasklistapp
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: TaskDatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var taskList: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = TaskDatabaseHelper(this)
        listView = findViewById(R.id.listViewTasks)

        val fab: FloatingActionButton = findViewById(R.id.fabAddTask)
        fab.setOnClickListener {
            // Lógica para adicionar nova tarefa
            showAddTaskDialog()
        }

        loadTasks()
    }

    private fun loadTasks() {
        taskList = databaseHelper.getAllTasks().toMutableList()
        val taskNames = taskList.map { it.name }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskNames)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = taskList[position]
            showUpdateDeleteDialog(selectedTask)
        }
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Adicionar Tarefa")

        val input = EditText(this)
        input.hint = "Nome da Tarefa"
        builder.setView(input)

        builder.setPositiveButton("Adicionar") { _, _ ->
            val taskName = input.text.toString()
            if (taskName.isNotEmpty()) {
                databaseHelper.addTask(taskName)
                loadTasks()
                Toast.makeText(this, "Tarefa adicionada!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showUpdateDeleteDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Gerenciar Tarefa")
        builder.setMessage("O que você deseja fazer com a tarefa '${task.name}'?")

        // Opção de Editar
        builder.setPositiveButton("Editar") { _, _ ->
            showEditTaskDialog(task)
        }

        // Opção de Deletar
        builder.setNegativeButton("Excluir") { _, _ ->
            val isDeleted = databaseHelper.deleteTask(task.id)
            if (isDeleted) {
                Toast.makeText(this, "Tarefa excluída!", Toast.LENGTH_SHORT).show()
                loadTasks() // Atualiza a lista
            } else {
                Toast.makeText(this, "Erro ao excluir tarefa.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão de Cancelar
        builder.setNeutralButton("Cancelar", null)

        builder.show()
    }

    private fun showEditTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Tarefa")

        val input = EditText(this)
        input.setText(task.name)
        builder.setView(input)

        builder.setPositiveButton("Salvar") { _, _ ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                databaseHelper.updateTask(task.id, newName)
                Toast.makeText(this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show()
                loadTasks() // Atualiza a lista
            } else {
                Toast.makeText(this, "O nome não pode estar vazio.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

}
