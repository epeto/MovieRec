
import csv

# Construye el diccionario con idMovie y las url de las películas.
def buildDicc():
    archivo = open("links.csv")
    csvreader = csv.reader(archivo)
    fstLine = csvreader.__next__()
    diccionario = {}
    for row in csvreader:
        llave = row[0]
        imdbId = "http://www.imdb.com/title/tt"+row[1]+"/"
        tmdbId = "https://www.themoviedb.org/movie/"+row[2]
        diccionario[llave] = (imdbId, tmdbId)
    return diccionario

# Pega las url de las películas al archivo con 'movieId'
def pegaURL(diccionario, archivo):
    csvreader = csv.reader(open(archivo+'.csv'))
    fstLine = csvreader.__next__()
    indIdMov = fstLine.index('movieId')
    salida = open(archivo+"_v3.csv", 'w')
    csvwriter = csv.writer(salida)
    fstLine.append("imdb")
    fstLine.append("themoviedb")
    csvwriter.writerow(fstLine)
    for fila in csvreader:
        url1, url2 = diccionario[fila[indIdMov]]
        fila.append(url1)
        fila.append(url2)
        csvwriter.writerow(fila)
    salida.close()

# Principal
def main():
    mapa = buildDicc()
    print("Escriba el nombre del archivo (sin el csv)")
    entrada = input()
    pegaURL(mapa, entrada)

main()