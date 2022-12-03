
import csv

def hastaComa(cadena):
    salida = ""
    for c in cadena:
        if c == ',':
            break
        else:
            salida += c
    return salida

def separaTitle(nombreArchivo):
    archivo = open(nombreArchivo+".csv")
    csvreader = csv.reader(archivo)
    fstLine = csvreader.__next__()
    indexTitle = fstLine.index('title')
    fstLine.insert(indexTitle+1, 'year')
    salida = open(nombreArchivo+"_v2.csv", 'w')
    csvwriter = csv.writer(salida)
    csvwriter.writerow(fstLine)
    for row in csvreader:
        row2 = row.copy()
        titulo = row[indexTitle]
        if titulo[len(titulo)-1] != ')':
            titulo += " (2016)"
        fstTitle = titulo[0:len(titulo)-7].strip()
        sndTitle = titulo[len(titulo)-6:len(titulo)].strip()
        tituloLimpio = hastaComa(fstTitle)
        year = sndTitle[1:5]
        row2[indexTitle] = tituloLimpio
        row2.insert(indexTitle+1, year)
        csvwriter.writerow(row2)
    archivo.close()
    salida.close()

def main():
    print("Ingrese el nombre del archivo que contiene la columna \'title\', pero sin la terminación csv.")
    entrada = input()
    separaTitle(entrada)

main()
