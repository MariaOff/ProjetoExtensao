data class Produto(
    val id: Int,
    val nome: String,
    var quantidade: Int,
    val preco: Double
)
class ProdutoAdapter(private val produtos: List<Produto>) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]
        holder.bind(produto)
    }

    override fun getItemCount(): Int = produtos.size

    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeProduto: TextView = itemView.findViewById(R.id.tvNomeProduto)
        private val quantidadeProduto: TextView = itemView.findViewById(R.id.tvQuantidadeProduto)

        fun bind(produto: Produto) {
            nomeProduto.text = produto.nome
            quantidadeProduto.text = "Quantidade: ${produto.quantidade}"

            if (produto.quantidade < 5) {
                quantidadeProduto.setTextColor(Color.RED)
            }
        }
    }
}
class MainActivity : AppCompatActivity() {

    private lateinit var produtoAdapter: ProdutoAdapter
    private lateinit var produtos: MutableList<Produto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        produtos = mutableListOf(
            Produto(1, "Leite condensado", 10, 5.99),
            Produto(2, "Farinha", 20, 6.49)
        )

        produtoAdapter = ProdutoAdapter(produtos)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = produtoAdapter

        val btnAdicionarProduto: Button = findViewById(R.id.btnAdicionarProduto)
        btnAdicionarProduto.setOnClickListener {
            // Aqui você pode abrir uma tela de cadastro de produtos
            adicionarProduto("Macarrão", 4, 2.50)
        }
    }

    private fun adicionarProduto(nome: String, quantidade: Int, preco: Double) {
        val produto = Produto(produtos.size + 1, nome, quantidade, preco)
        produtos.add(produto)
        produtoAdapter.notifyDataSetChanged()

        // Verifica se algum produto está com estoque baixo
        verificarEstoqueBaixo(produto)
    }

    private fun verificarEstoqueBaixo(produto: Produto) {
        if (produto.quantidade < 5) {
            enviarNotificacao(produto)
        }
    }

    private fun enviarNotificacao(produto: Produto) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "estoque_baixo_channel",
                "Estoque Baixo",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "estoque_baixo_channel")
            .setContentTitle("Estoque baixo: ${produto.nome}")
            .setContentText("O estoque de ${produto.nome} está abaixo de 5 unidades!")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }
}
