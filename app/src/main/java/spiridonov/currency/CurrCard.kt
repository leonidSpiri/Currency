package spiridonov.currency

// класс для создания объекта каждой отдельной валюты и использование его для адаптера списка
class CurrCard(var code: String, var name: String, var value: String, var previous: String)