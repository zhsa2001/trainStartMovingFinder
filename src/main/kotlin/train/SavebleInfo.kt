package train

interface SavebleInfo {
    abstract fun info (tab: String = "\t"): String
}