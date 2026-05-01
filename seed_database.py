"""
Seed script — inserts 100 fake Films + supporting Categories & Acteurs
into the MySQL database used by the Spring Boot app.

Requirements:
    pip install mysql-connector-python faker
"""

import random
import mysql.connector
from faker import Faker

# ── DB config (mirrors application.yml) ─────────────────────────────────────
DB = dict(
    host="localhost",
    port=3306,
    database="db",
    user="ahmed",
    password="2003",
)

fake = Faker("fr_FR")          # French locale for realistic names
random.seed(42)

# ── Seed data pools ──────────────────────────────────────────────────────────
CATEGORIES = [
    "Action", "Comédie", "Drame", "Horreur", "Science-Fiction",
    "Romance", "Thriller", "Animation", "Documentaire", "Aventure",
]

FIRST_NAMES = [
    "Jean", "Marie", "Pierre", "Sophie", "Thomas", "Isabelle",
    "Nicolas", "Camille", "Julien", "Céline", "François", "Laura",
    "Antoine", "Amélie", "Maxime", "Lucie", "Romain", "Emma",
    "Alexandre", "Chloé", "Éric", "Nathalie", "David", "Aurélie",
    "Mathieu", "Stéphanie", "Olivier", "Valérie", "Sébastien", "Manon",
]

LAST_NAMES = [
    "Martin", "Bernard", "Dubois", "Thomas", "Robert", "Richard",
    "Petit", "Durand", "Leroy", "Moreau", "Simon", "Laurent",
    "Lefebvre", "Michel", "Garcia", "David", "Bertrand", "Roux",
    "Vincent", "Fournier", "Morel", "Girard", "André", "Lefevre",
    "Mercier", "Dupont", "Lambert", "Bonnet", "François", "Martinez",
]

FILM_ADJECTIVES = [
    "Dernier", "Premier", "Grand", "Petit", "Mystérieux", "Étrange",
    "Sombre", "Lumineux", "Rapide", "Lent", "Ancien", "Nouveau",
    "Secret", "Perdu", "Trouvé", "Oublié", "Maudit", "Béni",
]

FILM_NOUNS = [
    "Voyage", "Destin", "Ombre", "Lumière", "Rêve", "Cauchemar",
    "Héros", "Ennemi", "Amour", "Haine", "Monde", "Univers",
    "Cœur", "Âme", "Espoir", "Vérité", "Mensonge", "Mystère",
    "Bataille", "Victoire", "Défaite", "Chemin", "Frontière", "Horizon",
]


def unique_title(used: set) -> str:
    for _ in range(1000):
        title = f"Le {random.choice(FILM_ADJECTIVES)} {random.choice(FILM_NOUNS)}"
        if title not in used:
            used.add(title)
            return title
    # Fallback: append a number
    base = f"Le {random.choice(FILM_ADJECTIVES)} {random.choice(FILM_NOUNS)}"
    n = 2
    while f"{base} {n}" in used:
        n += 1
    used.add(f"{base} {n}")
    return f"{base} {n}"


# ── Main seeding logic ───────────────────────────────────────────────────────
def seed():
    conn = mysql.connector.connect(**DB)
    cur = conn.cursor()

    print("Connected to database.")

    # 1. Insert categories ────────────────────────────────────────────────────
    category_ids = []
    for nom in CATEGORIES:
        cur.execute(
            "INSERT IGNORE INTO category (nom) VALUES (%s)", (nom,)
        )
    conn.commit()
    cur.execute("SELECT id FROM category")
    category_ids = [row[0] for row in cur.fetchall()]
    print(f"  {len(category_ids)} categories ready.")

    # 2. Insert acteurs (40 actors) ───────────────────────────────────────────
    actor_ids = []
    for _ in range(40):
        nom    = random.choice(LAST_NAMES)
        prenom = random.choice(FIRST_NAMES)
        active = random.choice([True, False])
        cur.execute(
            "INSERT INTO acteur (nom, prenom, active) VALUES (%s, %s, %s)",
            (nom, prenom, active),
        )
        actor_ids.append(cur.lastrowid)
    conn.commit()
    print(f"  {len(actor_ids)} acteurs inserted.")

    # 3. Insert 100 films ─────────────────────────────────────────────────────
    used_titles: set = set()
    film_ids = []

    for _ in range(100):
        titre        = unique_title(used_titles)
        description  = fake.paragraph(nb_sentences=3)
        annepartution = random.randint(1970, 2025)
        category_id  = random.choice(category_ids)

        cur.execute(
            """
            INSERT INTO film (titre, description, annepartution, category_id)
            VALUES (%s, %s, %s, %s)
            """,
            (titre, description, annepartution, category_id),
        )
        film_ids.append(cur.lastrowid)

    conn.commit()
    print(f"  {len(film_ids)} films inserted.")

    # 4. Assign 1–4 random actors to each film ────────────────────────────────
    # JPA names the join table "film_acteurs" with columns films_id / acteurs_id
    for film_id in film_ids:
        chosen = random.sample(actor_ids, k=random.randint(1, 4))
        for actor_id in chosen:
            cur.execute(
                "INSERT IGNORE INTO film_acteurs (films_id, acteurs_id) VALUES (%s, %s)",
                (film_id, actor_id),
            )
    conn.commit()
    print("  Actor–Film relations inserted.")

    cur.close()
    conn.close()
    print("\nDone! Database seeded with 100 films, 40 actors, 10 categories.")


if __name__ == "__main__":
    seed()
