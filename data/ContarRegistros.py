import pandas as pd

# INNER JOIN de ratings con movies
def full_join():
    df = pd.read_csv('ratings.csv', encoding='utf8', encoding_errors='ignore')
    df1 = pd.read_csv('movies.csv', encoding='utf8', encoding_errors='ignore')
    df = df.merge(df1, how='inner', on='movieId')
    df.to_csv('out.csv')
    print(len(df.index))
    print(df)

# INNER JOIN con los usuarios
def full_join_users():
    df = pd.read_csv('out.csv', encoding='utf8', encoding_errors='ignore')
    df1 = pd.read_csv('users.csv', encoding='utf8', encoding_errors='ignore')
    df = df.merge(df1, how='inner', on='userId')
    df.to_csv('out-users.csv')
    print(df)

# Obtener 8000 registros del archivo fuente
def get_8000():
    df = pd.read_csv('out-users.csv', encoding='utf8', encoding_errors='ignore')
    df = df.head(8000)
    df.to_csv('out-users-8000.csv')
    print(df)


"""
df = pd.read_csv('db/out-8000.csv', encoding='utf8', encoding_errors='ignore')
df = df.drop(['Unnamed: 0'], axis=1)
df.to_csv('db/out-users-8000.csv')

genome-scores.csv -> 14862528
genome-tags.csv -> 1128
links.csv -> 58098
movies.csv -> 58098
ratings.csv -> 27753444
tags.csv -> 1108997
"""
