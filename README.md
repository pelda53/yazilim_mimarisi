# LogFlow — Increment 2

Bu sürüm, ödevdeki **Artım 2 — Tipli Kayıtlar ve İlk Gerçek Filtre** gereksinimlerine göre hazırlanmıştır.

## Mimari

```text
access-small.log
      |
      v
FileLineSource
      |
      v
ParserStage
      |
      v
ConsoleSink
```

Pipeline artık `String` yerine `LogRecord` taşır. `LogRecord` immutable bir Java `record` tipidir.

`attributes` alanı sonraki haftalarda parser'ın bugün bilmediği yeni bilgilerin eklenebilmesi için genişleme noktasıdır.

Hatalı satırlar bu sürümde atlanır ve sayılır. Ödevde belirtildiği gibi hatalı satırların ayrıntılı yönetimi sonraki haftaya bırakılmıştır.

## Derleme

Java 17 veya üstü gerekir.

Basit derleme:

```bash
rm -rf out
mkdir -p out
javac -d out $(find src/logflow -name "*.java")
```

Çalıştırma:

```bash
java -cp out logflow.Main data/access-small.log
```

Son satırda:

```text
Invalid lines: N
```

görülmelidir.

## JUnit 5 testleri

Maven kullanılıyorsa:

```bash
mvn test
```

Testler `ParserStage`'i doğrudan oluşturur ve `CollectingEmitter` ile çıktıyı bellekte yakalar. Dosya sistemine erişmez.

## En az 8 test

1. Geçerli satır
2. Eksik alan
3. Hatalı zaman damgası
4. Hatalı durum kodu
5. Boş satır
6. Fazladan boşluk
7. Boşluk içeren tırnaklı user agent
8. Query string içeren satır

## Satır kapsama

Maven/JaCoCo gibi bir coverage aracı eklenmediği için burada uydurma bir yüzde verilmemiştir. Teslimden önce kullanılan coverage aracıyla gerçek yüzde ölçülüp bu bölüme yazılmalıdır.

## Demo soruları

### 1. Parser'ı eklemek için hangi dosyalar değişti?

`LogRecord`, `ParserStage`, `ConsoleSink`, `OrderPipeline` ve `Main` yeni mimariye bağlandı. Ayrıca `CollectingEmitter` ve `ParserStageTest` test altyapısını oluşturur.

### 2. Parser testi dosya okumadan nasıl çalışıyor?

Testte `ParserStage` doğrudan oluşturulur. Bir log satırı `parser.accept(...)` ile stage'e verilir. Stage'in çıktısı `CollectingEmitter` tarafından bellekte tutulur. Böylece test gerçek dosyaya ihtiyaç duymaz; parser'ın davranışı izole şekilde test edilir.

## Değişiklik günlüğü — v2

- `LogRecord` domain modeli eklendi.
- `ParserStage` eklendi.
- `ConsoleSink` artık `LogRecord` yazdırıyor.
- Pipeline parser aşamasıyla güncellendi.
- Hatalı satır sayacı eklendi.
- `CollectingEmitter` test double'ı eklendi.
- Parser için 8 JUnit 5 testi eklendi.
- README ve v2 açıklaması eklendi.
