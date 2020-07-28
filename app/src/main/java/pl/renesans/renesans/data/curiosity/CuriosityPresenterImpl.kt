package pl.renesans.renesans.data.curiosity

class CuriosityPresenterImpl: CuriosityPresenter {

    private val curiositiesList = mutableListOf(
        "Leonardo Da Vinci kupował zwierzęta trzymane w klatkach, tylko po to, by wypuścić je na wolność",
        "Z sympatii do zwierząt, Leonardo Da Vinci prowadził wegetariański tryb życia.",
        "Leonardo da Vinci pisał od prawej do lewej, pismem lustrzanym. Najprawdopodobniej wynikało to z tego, że był leworęczny.",
        "Poza astronomią Kopernik zajmował się prawem, matematyką, ekonomią, astrologią, strategią wojskową. Był również tłumaczem i lekarzem.",
        "Kopernik stanowił przykład prawdziwego człowieka renesansu. Znał łacinę i grekę, a podczas studiów we Włoszech, spotkał się z Leonardem da Vinci.",
        "W latach 1616 – 1822 dzieło Kopernika „De revolutionibus orbium coelestium” znajdowało się na indeksie ksiąg zakazanych, nazywanych inaczej księgami zatrutymi.",
        "Jeden z obrazów Rafaela Santiego – Madonna Sykstyńska – został zakupiony przez polskiego króla Augusta II Sasa u zarania XVIII wieku za sumę z budżetu Rzeczpospolitej.",
        "Galileusz jako jeden z pierwszych osób, używał do obserwacji planet, gwizd i Księżyca – teleskopu.",
        "Przełomowy wynalazek Galileusza, jakim był teleskop, pozwolił mu potwierdzić teorię heliocentryczną Kopernika.",
        "Szekspir wpłynął na język angielski bardziej niż jakikolwiek inny autor w historii dzięki temu, że spopularyzował i wymyślił wiele słów i zwrotów.",
        "Zasób słownictwa Szekspira był co najmniej dwukrotnie większy niż przeciętnego rozmówcy.",
        "Oprócz pisania licznych sztuk i sonetów, Szekspir był również aktorem, który wykonywał wiele własnych sztuk, a także innych dramaturgów.",
        "Portret Mona Lisy był malowany przez Leonarda Da Vinciego przez aż trzy lata.")
    private var currentCuriosities: MutableList<String>? = null
    private var currentCuriosity = 0

    override fun getRandomCuriosity(): String {
        if(currentCuriosities == null || currentCuriosity >= currentCuriosities!!.size){
            curiositiesList.shuffle()
            currentCuriosities = curiositiesList
            currentCuriosity = 0
        } else currentCuriosity++
        return "Czy o tym wiedziałeś? " + currentCuriosities!![currentCuriosity]
    }
}