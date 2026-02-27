package com.example.login.data

import com.example.login.R
import com.example.login.model.AlbumDetalleUI
import com.example.login.model.CancionDetalleUI

// ── Datos locales quemados de Detalle de Álbum ──
// IDs 1-9  → álbumes de HomeData
// IDs 101-105 → álbumes de DescubreData (offset +100 sobre su id original)
object AlbumDetalleData {

    private val todos = listOf(

        // ───────── QUIET RIOT ─────────
        AlbumDetalleUI(
            id = 1,
            nombre = "Cum on Feel the Noize",
            artista = "Quiet Riot",
            año = "1983",
            genero = "Heavy Metal",
            descripcion = "Éxito definitivo de Quiet Riot que catapultó al grupo a la cima del rock duro de los 80. Con una energía desbordante y riffs memorables, el álbum conquistó las listas de todo el mundo.",
            imagenRes = R.drawable.album1,
            duracionTotal = "38 min",
            calificacionPromedio = 4.2f,
            totalResenas = 312,
            canciones = listOf(
                CancionDetalleUI(1, "Cum on Feel the Noize", "3:45"),
                CancionDetalleUI(2, "Slick Black Cadillac", "3:52"),
                CancionDetalleUI(3, "Love's a Bitch", "4:01"),
                CancionDetalleUI(4, "Breathless", "4:18"),
                CancionDetalleUI(5, "Run for Cover", "3:55"),
                CancionDetalleUI(6, "Condition Critical", "3:30"), CancionDetalleUI(7, "Bad Boy", "3:40"),
                CancionDetalleUI(8, "Party All Night", "4:12")
            )
        ),
        AlbumDetalleUI(
            id = 2,
            nombre = "Mama Weer All Crazee Now",
            artista = "Quiet Riot",
            año = "1983",
            genero = "Heavy Metal",
            descripcion = "Versión potente del clásico de Slade que se convirtió en himno del metal de los 80. Un álbum crudo y enérgico que consolida el sonido característico de la banda.",
            imagenRes = R.drawable.album2,
            duracionTotal = "41 min",
            calificacionPromedio = 4.0f,
            totalResenas = 198,
            canciones = listOf(
                CancionDetalleUI(1, "Mama Weer All Crazee Now", "3:38"),
                CancionDetalleUI(2, "Thunderbird", "4:05"),
                CancionDetalleUI(3, "The Wild and the Young", "3:50"),
                CancionDetalleUI(4, "Sign of the Times", "4:22"),
                CancionDetalleUI(5, "Stomp Your Hands, Clap Your Feet", "3:44"),
                CancionDetalleUI(6, "Metal Health", "4:10")
            )
        ),
        AlbumDetalleUI(
            id = 3,
            nombre = "The Wild The You",
            artista = "Quiet Riot",
            año = "1984",
            genero = "Glam Metal",
            descripcion = "Álbum de seguimiento que muestra la madurez musical del grupo con composiciones más elaboradas y producción de alta calidad.",
            imagenRes = R.drawable.album3,
            duracionTotal = "43 min",
            calificacionPromedio = 3.8f,
            totalResenas = 142,
            canciones = listOf(
                CancionDetalleUI(1, "The Wild and the Young", "3:55"),
                CancionDetalleUI(2, "Twilight Hotel", "4:08"),
                CancionDetalleUI(3, "Winners Take All", "3:42"),
                CancionDetalleUI(4, "Fire the Witch", "4:30"),
                CancionDetalleUI(5, "Put Up or Shut Up", "3:58")
            )
        ),

        // ───────── BAD BUNNY ─────────
        AlbumDetalleUI(
            id = 4,
            nombre = "X100PRE",
            artista = "Bad Bunny",
            año = "2018",
            genero = "Reggaetón / Trap Latino",
            descripcion = "Álbum debut de Bad Bunny que redefinió el reggaetón moderno. Con 21 canciones, el proyecto mezcla trap, R&B y ritmos latinos en una propuesta fresca y auténtica.",
            imagenRes = R.drawable.album4,
            duracionTotal = "56 min",
            calificacionPromedio = 4.6f,
            totalResenas = 1850,
            canciones = listOf(
                CancionDetalleUI(1, "Ni Bien Ni Mal", "2:44"),
                CancionDetalleUI(2, "Estamos Bien", "3:28"),
                CancionDetalleUI(3, "Caro", "2:58"),
                CancionDetalleUI(4, "La Romana", "2:52"),
                CancionDetalleUI(5, "Amorfoda", "3:06"),
                CancionDetalleUI(6, "Solo de Mi", "2:59"),
                CancionDetalleUI(7, "Mía", "3:20"),
                CancionDetalleUI(8, "Otra Noche en Miami", "3:14"),
                CancionDetalleUI(9, "Si Tu Novio Te Deja Sola", "3:05"),
                CancionDetalleUI(10, "Sensualidad", "3:35")
            )
        ),
        AlbumDetalleUI(
            id = 5,
            nombre = "Un Verano Sin Ti",
            artista = "Bad Bunny",
            año = "2022",
            genero = "Reggaetón / Latin Pop",
            descripcion = "Obra maestra que celebra la cultura puertorriqueña. El álbum más escuchado del mundo en 2022 en Spotify, con 23 canciones que van del reggaetón al dembow, cumbia y R&B.",
            imagenRes = R.drawable.album5,
            duracionTotal = "81 min",
            calificacionPromedio = 4.9f,
            totalResenas = 5200,
            canciones = listOf(
                CancionDetalleUI(1, "El Apagón", "4:13"),
                CancionDetalleUI(2, "Tití Me Preguntó", "4:02"),
                CancionDetalleUI(3, "Después de la Playa", "2:55"),
                CancionDetalleUI(4, "Me Porto Bonito", "2:56"),
                CancionDetalleUI(5, "Neverita", "3:15"),
                CancionDetalleUI(6, "Moscow Mule", "3:27"),
                CancionDetalleUI(7, "Ojitos Lindos", "3:22"),
                CancionDetalleUI(8, "Un Verano Sin Ti", "3:40"),
                CancionDetalleUI(9, "Party", "2:58"),
                CancionDetalleUI(10, "Aguacero", "3:10")
            )
        ),
        AlbumDetalleUI(
            id = 6,
            nombre = "Las Que No Iban a Salir",
            artista = "Bad Bunny",
            año = "2020",
            genero = "Reggaetón / Trap",
            descripcion = "EP sorpresa lanzado durante la pandemia con 8 canciones introspectivas. Un proyecto íntimo que muestra el lado más personal del artista.",
            imagenRes = R.drawable.album6,
            duracionTotal = "27 min",
            calificacionPromedio = 4.4f,
            totalResenas = 920,
            canciones = listOf(
                CancionDetalleUI(1, "Ignorantes", "3:40"),
                CancionDetalleUI(2, "En Casita", "3:02"),
                CancionDetalleUI(3, "Pero Ya No", "2:58"),
                CancionDetalleUI(4, "Cuando Era Bebé", "3:20"),
                CancionDetalleUI(5, "Yo Perreo Sola", "2:55"),
                CancionDetalleUI(6, "Pa Ti", "3:10"),
                CancionDetalleUI(7, "Safaera", "4:05"),
                CancionDetalleUI(8, "Andrea", "3:25")
            )
        ),

        // ───────── QUEEN ─────────
        AlbumDetalleUI(
            id = 7,
            nombre = "A Night at the Opera",
            artista = "Queen",
            año = "1975",
            genero = "Rock Clásico",
            descripcion = "Considerado uno de los mejores álbumes de la historia. Contiene 'Bohemian Rhapsody', una de las canciones más innovadoras jamás grabadas. Un viaje épico por el rock, la ópera y el music-hall.",
            imagenRes = 0,
            duracionTotal = "43 min",
            calificacionPromedio = 5.0f,
            totalResenas = 8700,
            canciones = listOf(
                CancionDetalleUI(1, "Death on Two Legs", "3:43"),
                CancionDetalleUI(2, "Lazing on a Sunday Afternoon", "1:08"),
                CancionDetalleUI(3, "I'm in Love with My Car", "3:05"),
                CancionDetalleUI(4, "You're My Best Friend", "2:52"),
                CancionDetalleUI(5, "Bohemian Rhapsody", "5:55"),
                CancionDetalleUI(6, "Love of My Life", "3:38"),
                CancionDetalleUI(7, "Good Company", "3:25"),
                CancionDetalleUI(8, "39", "3:29"),
                CancionDetalleUI(9, "God Save the Queen", "1:11")
            )
        ),
        AlbumDetalleUI(
            id = 8,
            nombre = "The Game",
            artista = "Queen",
            año = "1980",
            genero = "Rock / Pop Rock",
            descripcion = "El álbum más exitoso comercialmente de Queen. Incluye 'Crazy Little Thing Called Love' y 'Another One Bites the Dust', demostrando la versatilidad del grupo.",
            imagenRes = 0,
            duracionTotal = "48 min",
            calificacionPromedio = 4.8f,
            totalResenas = 4100,
            canciones = listOf(
                CancionDetalleUI(1, "Play the Game", "3:32"),
                CancionDetalleUI(2, "Dragon Attack", "4:19"),
                CancionDetalleUI(3, "Another One Bites the Dust", "3:36"),
                CancionDetalleUI(4, "Need Your Loving Tonight", "2:51"),
                CancionDetalleUI(5, "Crazy Little Thing Called Love", "2:43"),
                CancionDetalleUI(6, "Rock It", "3:35"),
                CancionDetalleUI(7, "Don't Try Suicide", "3:52"),
                CancionDetalleUI(8, "Sail Away Sweet Sister", "3:32"),
                CancionDetalleUI(9, "Coming Soon", "2:50"),
                CancionDetalleUI(10, "Save Me", "3:48")
            )
        ),
        AlbumDetalleUI(
            id = 9,
            nombre = "News of the World",
            artista = "Queen",
            año = "1977",
            genero = "Rock / Arena Rock",
            descripcion = "Hogar de los himnos deportivos definitivos. 'We Will Rock You' y 'We Are the Champions' se convirtieron en canciones universales que trascienden generaciones.",
            imagenRes = 0,
            duracionTotal = "44 min",
            calificacionPromedio = 4.9f,
            totalResenas = 6300,
            canciones = listOf(
                CancionDetalleUI(1, "We Will Rock You", "2:01"),
                CancionDetalleUI(2, "We Are the Champions", "2:59"),
                CancionDetalleUI(3, "Sheer Heart Attack", "3:26"),
                CancionDetalleUI(4, "All Dead, All Dead", "3:09"),
                CancionDetalleUI(5, "Spread Your Wings", "4:34"),
                CancionDetalleUI(6, "Fight from the Inside", "3:03"),
                CancionDetalleUI(7, "Get Down, Make Love", "3:51"),
                CancionDetalleUI(8, "Sleeping on the Sidewalk", "3:07"),
                CancionDetalleUI(9, "Who Needs You", "2:59"),
                CancionDetalleUI(10, "It's Late", "6:26"),
                CancionDetalleUI(11, "My Melancholy Blues", "3:27")
            )
        ),

        // ───────── DESCUBRE (IDs 101-105) ─────────
        AlbumDetalleUI(
            id = 101,
            nombre = "Midnights",
            artista = "Taylor Swift",
            año = "2022",
            genero = "Synth-Pop / Indie Pop",
            descripcion = "Un viaje por 13 noches de insomnio donde Taylor Swift explora sus miedos, deseos y reflexiones más íntimas. Un álbum oscuro y hipnótico que redefine su sonido.",
            imagenRes = R.drawable.album_midnights,
            duracionTotal = "44 min",
            calificacionPromedio = 4.7f,
            totalResenas = 9800,
            canciones = listOf(
                CancionDetalleUI(1, "Lavender Haze", "4:23"),
                CancionDetalleUI(2, "Maroon", "3:38"),
                CancionDetalleUI(3, "Anti-Hero", "3:20"),
                CancionDetalleUI(4, "Snow on the Beach", "4:16"),
                CancionDetalleUI(5, "Midnight Rain", "2:54"),
                CancionDetalleUI(6, "Question...?", "3:43"),
                CancionDetalleUI(7, "Vigilante Shit", "2:44"),
                CancionDetalleUI(8, "Bejeweled", "3:15"),
                CancionDetalleUI(9, "Labyrinth", "4:09"),
                CancionDetalleUI(10, "Karma", "3:25"),
                CancionDetalleUI(11, "Sweet Nothing", "3:09"),
                CancionDetalleUI(12, "Mastermind", "3:12"),
                CancionDetalleUI(13, "Hits Different", "3:49")
            )
        ),
        AlbumDetalleUI(
            id = 102,
            nombre = "Renaissance",
            artista = "Beyoncé",
            año = "2022",
            genero = "Dance / House / R&B",
            descripcion = "Tributo a la música dance y house de la comunidad LGBTQ+. Un álbum continuo y bailable que celebra la libertad, la identidad y la danza sin parar.",
            imagenRes = R.drawable.album_renaissance,
            duracionTotal = "62 min",
            calificacionPromedio = 4.8f,
            totalResenas = 7400,
            canciones = listOf(
                CancionDetalleUI(1, "I'M THAT GIRL", "2:33"),
                CancionDetalleUI(2, "COZY", "3:28"),
                CancionDetalleUI(3, "ALIEN SUPERSTAR", "3:50"),
                CancionDetalleUI(4, "CUFF IT", "3:36"),
                CancionDetalleUI(5, "ENERGY", "2:26"),
                CancionDetalleUI(6, "BREAK MY SOUL", "4:38"),
                CancionDetalleUI(7, "CHURCH GIRL", "4:44"),
                CancionDetalleUI(8, "PLASTIC OFF THE SOFA", "4:05"),
                CancionDetalleUI(9, "VIRGO'S GROOVE", "6:08"),
                CancionDetalleUI(10, "MOVE", "3:11"),
                CancionDetalleUI(11, "HEATED", "4:52"),
                CancionDetalleUI(12, "THIQUE", "3:47"),
                CancionDetalleUI(13, "ALL UP IN YOUR MIND", "2:59"),
                CancionDetalleUI(14, "AMERICA HAS A PROBLEM", "3:32"),
                CancionDetalleUI(15, "PURE/HONEY", "4:47"),
                CancionDetalleUI(16, "SUMMER RENAISSANCE", "4:08")
            )
        ),
        AlbumDetalleUI(
            id = 103,
            nombre = "Un Verano Sin Ti",
            artista = "Bad Bunny",
            año = "2022",
            genero = "Reggaetón / Latin Pop",
            descripcion = "El álbum más escuchado del año. Obra maestra de la cultura puertorriqueña que fusiona reggaetón, dembow, cumbia y R&B.",
            imagenRes = R.drawable.album5,
            duracionTotal = "81 min",
            calificacionPromedio = 4.9f,
            totalResenas = 5200,
            canciones = listOf(
                CancionDetalleUI(1, "El Apagón", "4:13"),
                CancionDetalleUI(2, "Tití Me Preguntó", "4:02"),
                CancionDetalleUI(3, "Me Porto Bonito", "2:56"),
                CancionDetalleUI(4, "Neverita", "3:15"),
                CancionDetalleUI(5, "Moscow Mule", "3:27"),
                CancionDetalleUI(6, "Ojitos Lindos", "3:22")
            )
        ),
        AlbumDetalleUI(
            id = 104,
            nombre = "Harry's House",
            artista = "Harry Styles",
            año = "2022",
            genero = "Pop / Indie Pop / Funk",
            descripcion = "Tercer álbum de Harry Styles, un proyecto íntimo y ecléctico que mezcla pop japonés, funk y rock. Ganador del Grammy al Álbum del Año 2023.",
            imagenRes = R.drawable.album_harrys_house,
            duracionTotal = "42 min",
            calificacionPromedio = 4.6f,
            totalResenas = 6100,
            canciones = listOf(
                CancionDetalleUI(1, "Music for a Sushi Restaurant", "3:12"),
                CancionDetalleUI(2, "Late Night Talking", "2:57"),
                CancionDetalleUI(3, "Grapejuice", "3:00"),
                CancionDetalleUI(4, "As It Was", "2:37"),
                CancionDetalleUI(5, "Daylight", "3:57"),
                CancionDetalleUI(6, "Little Freak", "3:32"),
                CancionDetalleUI(7, "Matilda", "4:05"),
                CancionDetalleUI(8, "Cinema", "3:48"),
                CancionDetalleUI(9, "Daydreaming", "2:59"),
                CancionDetalleUI(10, "Keep Driving", "2:21"),
                CancionDetalleUI(11, "Satellite", "3:56"),
                CancionDetalleUI(12, "Boyfriends", "3:43"),
                CancionDetalleUI(13, "Love of My Life", "3:43")
            )
        ),
        AlbumDetalleUI(
            id = 105,
            nombre = "Dawn FM",
            artista = "The Weeknd",
            año = "2022",
            genero = "Synth-Pop / R&B",
            descripcion = "Experiencia conceptual que simula una emisora de radio en el purgatorio. The Weeknd guía al oyente a través de su muerte y renacimiento con producción impecable.",
            imagenRes = R.drawable.album_dawn_fm,
            duracionTotal = "52 min",
            calificacionPromedio = 4.7f,
            totalResenas = 5900,
            canciones = listOf(
                CancionDetalleUI(1, "Dawn FM", "1:36"),
                CancionDetalleUI(2, "Gasoline", "3:32"),
                CancionDetalleUI(3, "How Do I Make You Love Me?", "3:18"),
                CancionDetalleUI(4, "Take My Breath", "3:44"),
                CancionDetalleUI(5, "Sacrifice", "3:08"),
                CancionDetalleUI(6, "A Tale By Quincy", "1:24"),
                CancionDetalleUI(7, "Out of Time", "3:38"),
                CancionDetalleUI(8, "Here We Go… Again", "4:04"),
                CancionDetalleUI(9, "Best Friends", "3:03"),
                CancionDetalleUI(10, "Is There Someone Else?", "3:44"),
                CancionDetalleUI(11, "Starry Eyes", "3:16"),
                CancionDetalleUI(12, "Every Angel Is Terrifying", "1:41"),
                CancionDetalleUI(13, "Don't Break My Heart", "3:14"),
                CancionDetalleUI(14, "I Heard You're Married", "3:31"),
                CancionDetalleUI(15, "Less Than Zero", "3:38"),
                CancionDetalleUI(16, "Phantom Regret by Jim", "3:14")
            )
        )
    )

    /** Busca un álbum por su id único. Devuelve null si no existe. */
    fun findById(id: Int): AlbumDetalleUI? = todos.find { it.id == id }
}