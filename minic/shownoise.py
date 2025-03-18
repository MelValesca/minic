#!/usr/bin/env python3
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# reading the database
#data = pd.read_csv("out/perf2.csv", skiprows=2, names=['val', 'unit', 'name', 'runtime', 'pc'], usecols=[0,1,2,3,4])
data = pd.read_json("out/perf3.json")

print(data.dtypes)
print(data)

df = data

df['no'] = df.groupby('event')['counter-value'].transform(lambda x: (100.0 * x / x.median()))
#idf['t'] = df.groupby('event')['date'].transform(lambda x: str(x))
#tt = list(set(df['t'].values.tolist()))
#print(tt)
#df['tt'] = df.groupby('event')['time'].transform(lambda x: str(x))
df = df.reset_index()

df = df.loc[df['event'] != "migrations"]
df = df.loc[df['event'] != "task-clock"]

plt.tight_layout()
plt.subplots_adjust(hspace=0)

sns.set_theme()
sns.set_style("whitegrid")

order=['cpu_core/instructions/', 'cpu_core/cycles/', 'user_time', 'duration_time']
grouped = df.loc[:, ['no','event']].groupby('event').std().sort_values(by='no')
#g = sns.relplot(x="index", y="no", data=df, kind="scatter", palette="pastel", hue="event", legend=True)
g = sns.violinplot(data=df, x='event', y='no', inner='box', hue='event', order=order)
g.set(xlabel = None)
g.set(ylabel = None)
g.set(ylim=(98, 106))
sns.despine(left=True)
#plt.legend(labels = ['Thursday', 'Friday', 'Saturday', 'Sunday'])
print((df.groupby('event')['counter-value'].median()/1e9))
medians=(df.groupby('event')['counter-value'].median()/1e9).to_dict()
lab=[
        "instructions\n100%%=%.2fG" % medians['cpu_core/instructions/'],
        "cycles\n100%%=%.2fG" % medians['cpu_core/cycles/'],
        "usertime\n100%%=%.2fs" % medians['user_time'],
        "durée\n100%%=%.2fs" % medians['duration_time'],
        ]
g.set_xticklabels(lab)
#sns.move_legend(g, "upper right", title=None, frameon=False, labels=lab, ncol=2)



print(df)
#df.plot(subplots=False, legend=True) #, ylim=(0,2))
#df.plot(subplots=False, legend=True) #, ylim=(0,2))
#plt.subplots_adjust(hspace=0)
#plt.ylim(bottom=0)
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
