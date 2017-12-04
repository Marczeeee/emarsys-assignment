# emarsys-assignment

>## Leírás
>
>Képzeld el, hogy nyaralásokat kell tervezned, amelyek több úti célt is tartalmazhatnak,
>ezeket a helyeket egy-egy betű jelképezi. Mivel minimalizálni akarjuk a költségeket, így
>előfordul, hogy egy út során egy úti célt a másik elé kell venni, mert így olcsóbban
>utazhatunk. Például, hogyha x helyre úgy juthatunk el takarékosabban, hogy előbb y helyet
>érintjük akkor a nyaralást úgy kell terveznünk, hogy y előbb legyen x -nél. Ha nincs függőség
>a célok között akkor azok érintésének sorrendje nem számít.
>
>A feladat célja, hogy beolvassuk és értelmezzük a lehetséges úti célokat, ezek függőségeit
>és adjunk meg egy optimális útvonalat a nyaraláshoz.
>
>A feladathoz válassz egy olyan nyelvet amiben otthonosan érzed magad. Nem kell felületet
>készíteni, egy a feladatra létrehozott osztály tökéletes. Gondold végig a hibalehetőségeket is
>és készülj fel rájuk.
>
>## Néhány minta
>| Bemenet | Kimenet |
>| ------- | ------- |
>| x => | x |
>|<span>x => <br/>y => <br/> z => <br/></span>| a kimenet tartalmazza xyz -t, de a sorrend nem számít |
>|<span>x => <br/> y => z <br/> z => <br/></span> | a kimenet tartalmazza xyz -t, de z mindenképp az y előtt van, pl. xzy |
>|<span>u => <br/> v => w <br/> w => z <br/> x => u <br/> y => v <br/> z => <br/> | a kimenet tartalmazza mind a hat úti célt, de z a w előtt van, w a v előtt, v az y előtt és u az x előtt |
