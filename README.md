# Currency
currency exchange
Необходимо с сайта цб скачивать список валют, и после этого отображать его в виде списка.
С сайта загружается JSON объект, после чего обрабатывается и выводится в виде списка Recycle View.
Recycle View реализован с помощью List Adapter. За счет DataBindingUtil адаптер показывает один из двуз макетов (favorite_item или item). Вызов onCreateViewHolder максимально сокращен, благодаря чему при масштабировании приложения не происходит пересоздание вьюхолдеров.
Реализовано на шаблоне MVVM. Проект разбит на три слоя -  data (работа с данными, их скачка, обработка, сохрание в БД), domain(бизнес-логика) и presentation(IO).
Используются библиотеки Android JetPack (lifecycle-viewModel, Worker, swipeRefreshLayout), Kotlin Coroutines(CoroutineScope, Dispatchers), Room DB