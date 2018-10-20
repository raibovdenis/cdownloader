# Console Downloader

Утилита написана на **Java 8**. Для сборки проекта используется **gradle**. 

**Upd**. Сделано для https://habr.com/company/ecwid/blog/315228/.

### Инструкция

```sh
$ git clone https://github.com/raibovdenis/cdownloader.git
$ cd cdownloader
$ gradlew build (windows) or ./gradlew build (linux - try "chmod 777 ./gradlew" if permission error)
$ java -jar utility.jar -n 5 -l 300k -o output_folder -f links.txt
```
