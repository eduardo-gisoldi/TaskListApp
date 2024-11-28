package com.example.tasklistapp

// Importação das bibliotecas necessárias para o funcionamento da atividade.
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Classe principal da atividade do aplicativo.
class MainActivity : AppCompatActivity() {

    // Declaração das variáveis para manipular a base de dados, lista de tarefas e adaptação dos dados.
    private lateinit var databaseHelper: TaskDatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var taskList: MutableList<Task> = mutableListOf()

    // Método chamado na criação da atividade.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Define o layout da atividade.

        // Inicialização do banco de dados e do ListView.
        databaseHelper = TaskDatabaseHelper(this)
        listView = findViewById(R.id.listViewTasks)

        // Configuração do botão de ação flutuante para adicionar novas tarefas.
        val fab: FloatingActionButton = findViewById(R.id.fabAddTask)
        fab.setOnClickListener {
            // Chama o método para exibir o diálogo de adição de tarefa.
            showAddTaskDialog()
        }

        // Carrega as tarefas existentes na base de dados.
        loadTasks()
    }

    // Método para carregar as tarefas da base de dados e exibi-las na ListView.
    private fun loadTasks() {
        // Obtém todas as tarefas do banco de dados.
        taskList = databaseHelper.getAllTasks().toMutableList()
        // Extrai apenas os nomes das tarefas para exibição na ListView.
        val taskNames = taskList.map { it.name }
        // Configura o adaptador para a ListView com o layout simples de item.
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskNames)
        listView.adapter = adapter

        // Configura o evento de clique em um item da lista.
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = taskList[position]
            // Chama o método para exibir o diálogo de atualização e exclusão.
            showUpdateDeleteDialog(selectedTask)
        }
    }

    // Método para exibir o diálogo de adição de nova tarefa.
    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Adicionar Tarefa") // Título do diálogo.

        // Cria um campo de entrada de texto para o nome da tarefa.
        val input = EditText(this)
        input.hint = "Nome da Tarefa" // Define o texto de dica do campo.
        builder.setView(input)

        // Configuração do botão de adição.
        builder.setPositiveButton("Adicionar") { _, _ ->
            val taskName = input.text.toString()
            // Verifica se o campo não está vazio antes de adicionar.
            if (taskName.isNotEmpty()) {
                databaseHelper.addTask(taskName) // Adiciona a tarefa ao banco de dados.
                loadTasks() // Atualiza a lista de tarefas.
                Toast.makeText(this, "Tarefa adicionada!", Toast.LENGTH_SHORT).show()
            }
        }
        // Configura o botão de cancelamento do diálogo.
        builder.setNegativeButton("Cancelar", null)
        builder.show() // Exibe o diálogo.
    }

    // Método para exibir o diálogo de atualização e exclusão de uma tarefa existente.
    private fun showUpdateDeleteDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Gerenciar Tarefa")
        builder.setMessage("O que você deseja fazer com a tarefa '${task.name}'?")

        // Botão para editar a tarefa.
        builder.setPositiveButton("Editar") { _, _ ->
            showEditTaskDialog(task)
        }

        // Botão para excluir a tarefa.
        builder.setNegativeButton("Excluir") { _, _ ->
            val isDeleted = databaseHelper.deleteTask(task.id)
            if (isDeleted) {
                Toast.makeText(this, "Tarefa excluída!", Toast.LENGTH_SHORT).show()
                loadTasks() // Atualiza a lista após a exclusão.
            } else {
                Toast.makeText(this, "Erro ao excluir tarefa.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão de cancelamento do diálogo.
        builder.setNeutralButton("Cancelar", null)

        builder.show() // Exibe o diálogo.
    }

    // Método para exibir o diálogo de edição de uma tarefa existente.
    private fun showEditTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Tarefa")

        // Cria um campo de entrada de texto com o nome da tarefa atual.
        val input = EditText(this)
        input.setText(task.name)
        builder.setView(input)

        // Botão para salvar as alterações na tarefa.
        builder.setPositiveButton("Salvar") { _, _ ->
            val newName = input.text.toString()
            // Verifica se o campo não está vazio antes de atualizar.
            if (newName.isNotEmpty()) {
                databaseHelper.updateTask(task.id, newName) // Atualiza a tarefa no banco de dados.
                Toast.makeText(this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show()
                loadTasks() // Atualiza a lista após a edição.
            } else {
                Toast.makeText(this, "O nome não pode estar vazio.", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão de cancelamento do diálogo.
        builder.setNegativeButton("Cancelar", null)
        builder.show() // Exibe o diálogo.
    }
}
