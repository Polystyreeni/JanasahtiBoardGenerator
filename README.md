# JanasahtiBoardGenerator
Game board generator for my word game [Janasahti](https://github.com/Polystyreeni/Janasahti).

Word list is provided by Kotimaisten kielten keskus, which can be downloaded [Here](https://kaino.kotus.fi/sanat/nykysuomi/). 
List is licenced under GNU LGPL (Lesser General Public License), EUPL v.1.1 and CC3.0 licences. For more info, see the provided PDF file.

**Usage**
- Specify number of boards to generate (recommended to keep this low, since it will take a while, something along 10-20 per run is manageable).
- Specify minimum score requirement (higher scores take more time, but have proven to be more enjoyable to play, balanced values range from 100-160).
- Specify the output file to generate boards to (for example: boards1.xml). Can be an existing file, as long as it's a board file as well.
- Game board creation is completely random, so depending on your settings it will take a while. Whenever a succesful board is generated, board contents will be printed.
- Profit.

**Requirements:**
- Java JDK 17
- [Maven](https://maven.apache.org/)
- OR: An IDE with support for the above (VSCode with Java plugins, NetBeans etc)

**Building and running**
- Clone or download source files
- If running from command-line:
  * Locate to the project root folder
  * Build using `mvn package`
  * Program should be packaged as a single .jar file `root/target/boardgenerator-1.0.one-jar.jar`
  * Run the .jar file with command `java -jar target/boardgenerator-1.0.one-jar.jar`
- If running from IDE:
  * Hit build
