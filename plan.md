# 🛠️ Iterative Build Plan – Detailed Phases

## **Phase 1 – Bare Minimum Core**

🎯 Goal: Have a working journal app with basic users + journals.
Keep it dead simple, no advanced security yet.

* **User Module**

  * CRUD for users (create, read, update, delete).
  * Fields: `id`, `username`, `email`, `password (hashed)`, `createdAt`.
  * Simple login: check username + password, return a temporary token/session (no JWT yet).

* **Journal Module**

  * CRUD for journal entries.
  * Fields: `id`, `authorId`, `title`, `content`, `createdAt`, `updatedAt`.
  * Rule: Only the owner can modify/delete their journals.

✅ End of Phase 1: Each user can sign up, log in, and maintain their own journals.

---

## **Phase 2 – Basic Social Layer**

🎯 Goal: Add minimal social interactions (follows + likes).

* **Follow System**

  * Users can follow/unfollow others.
  * Track `followersCount` and `followingCount`.
  * Data model: `Follow { followerId, followingId, createdAt }`.

* **Like System**

  * Users can like/unlike journal entries.
  * Maintain `likeCount` on each journal.
  * Data model: `Like { userId, journalId, createdAt }`.

✅ End of Phase 2: Users can connect with others via follows & likes, journals feel “social.”

---

## **Phase 3 – Feeds & Explore**

🎯 Goal: Let users see content beyond their own journals.

* **Home Feed**

  * Show latest journals from users you follow.
  * Query: journals where `authorId ∈ followingIds`.
  * Sort: `createdAt DESC`.
  * Pagination: limit + offset.

* **Explore Feed**

  * Show all public journals (ignore private for now).
  * Sorted by recency.
  * Can later add filters (trending, popular, etc.).

✅ End of Phase 3: Users have a home feed and a global explore feed.

---

## **Phase 4 – Intermediate Features**

🎯 Goal: Add depth, privacy, and notifications.

* **Authentication Upgrade**

  * Replace basic auth with JWT (access + refresh tokens).
  * Secure password storage (bcrypt).

* **Visibility Controls**

  * Journal visibility: `private`, `followers-only`, `public`.
  * Enforce in feeds & likes.

* **Notifications**

  * New follower.
  * New like on your journal.
  * New journal from someone you follow.
  * Store in `Notification { userId, type, refId, createdAt, seen }`.

✅ End of Phase 4: Feeds are smarter, journals can have privacy, and users get notified of interactions.

---

## **Phase 5 – Advanced Feed + Search**

🎯 Goal: Make discovery and engagement more powerful.

* **Feed Ranking**

  * Order by recency primarily.
  * Tie-breaker: likeCount.
  * Prepare for personalization (Phase 6).

* **Search**

  * Full-text search on journal `title` and `content`.
  * Filter by: `author`, `tags`, `date range`.
  * Use MongoDB text indexes for efficient querying.

✅ End of Phase 5: App has proper discovery and more engaging feeds.

---

## **Phase 6 – Hardening & Ops**

🎯 Goal: Get ready for production.

* **Security**

  * JWT refresh cycle.
  * Input validation everywhere.
  * Rate limiting per IP/user.

* **Monitoring & Deployment**

  * API health checks.
  * Logging (structured).
  * Metrics (request count, errors, latency).
  * Dockerize backend + MongoDB.
  * CI/CD pipeline setup.

✅ End of Phase 6: App is production-grade, secure, and deployable.

---

👉 Each phase is a **working release**. You don’t wait until the end — you can deploy after every phase and let your team iterate.

Would you like me to also prepare a **suggested timeline (e.g., Phase 1 = 1 week, Phase 2 = 1.5 weeks, etc.)** so it feels like a sprint plan?
