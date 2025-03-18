#!/usr/bin/env python3
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# reading the database
#data = pd.read_csv("out/perf2.csv", skiprows=2, names=['val', 'unit', 'name', 'runtime', 'pc'], usecols=[0,1,2,3,4])
data = pd.read_json("out/perf3.json")

print(data.dtypes)
print(data)

# Scatter plot with day against tip
#plt.scatter(data['time'], data['counter-value'])

df = data#[data['event'] == 'task-clock']

#events = ['task-clock','cpu_atom/cycles/','cpu_core/cycles/','cpu_atom/instructions/','cpu_core/instructions/']
events = ['task-clock','cpu_core/cycles/','cpu_core/instructions/']

#df = data[data['event'].isin(events)]


#df['no'] = df.groupby('event')['counter-value'].transform(lambda x: (x - x.mean()) / x.std())
df['no'] = df.groupby('event')['counter-value'].transform(lambda x: (x / x.median()))

#print(df)

#df = data.groupby('event')

df = data.pivot(columns="event", values="no", index="date")
df = df.drop(columns='migrations')
df = data[data['event'] == 'user_time'].pivot(columns="engine", values="counter-value")

#df.boxplot('counter-value', 'event')
#df.boxplot('no', 'event')
#df.boxplot('counter-value', 'engine')

#fig, ax = plt.subplots(nrows=6)
#df.groupby('event')['counter-value'].plot(kind='line')

plt.tight_layout()
plt.subplots_adjust(hspace=0)


#data[data['event'] == 'user_time'].plot.bar(x='engine',y='counter-value')

sns.set_theme()
sns.set_style("whitegrid")
#sns.set_context("talk")
df = data[data['event'] == 'user_time']
df['engine'] = df['engine'].transform(lambda x: x.replace(" ", "\n"))
df['counter-value'] = df['counter-value'].transform(lambda x:x/1e9)
df['minicc2'] = df['engine'].transform(lambda x:"minicc2" in x)
df['native'] = df['engine'].transform(lambda x:"native" in x)
#df = df.loc[df['minicc2'] == False]
df = df.loc[df['native'] == False]
#df = df.loc[df['engine'] != "clang\nnative\n-O1"]
#df = df.loc[df['engine'] != "clang\nnative\n-O2"]
grouped = df.loc[:, ['counter-value','engine']].groupby('engine').median().sort_values(by='counter-value')
#sns.boxplot(x="engine", y="counter-value", data=df, order=grouped.index)
g = sns.catplot(x="engine", y="counter-value", data=df, order=grouped.index, kind="bar", palette="pastel", edgecolor=".6", hue="source", legend=True)
#data[data['event'] == 'user_time'].boxplot(column='counter-value',by='engine')
sns.move_legend(g, "upper center", title=None)
g.set(xlabel = None)
g.set(ylabel = None)
#plt.yscale('log')
sns.despine(left=True)


print(df)
#df.plot(subplots=False, legend=True) #, ylim=(0,2))
#df.plot(subplots=False, legend=True) #, ylim=(0,2))
#plt.subplots_adjust(hspace=0)
plt.ylim(bottom=0)
plt.xticks(rotation=0)
plt.tight_layout()
#plt.title('')
#plt.xlabel('')
#plt.xticks([])
#plt.yticks([])
#plt.xlabel('event')
#plt.ylabel('normalized value')

# Adding Title to the Plot
#plt.title("Scatter Plot")
 
# Setting the X and Y labels
#plt.xlabel('Day')
#plt.ylabel('Tip')
 
#plt.show()
plt.savefig('benevent.pdf')
