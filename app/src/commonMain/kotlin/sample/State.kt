package sample

interface State {
    fun launch() = this

    infix fun next(action: Action): State
}

interface StateRunner {
    fun performEach(): State
}

sealed class Action {
    object StartListen : Action()
    object StartRecord : Action()
    object Stop : Action()
    object Review : Action()
    object Reset : Action()
}

